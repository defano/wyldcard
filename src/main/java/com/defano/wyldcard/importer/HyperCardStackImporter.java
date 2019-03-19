package com.defano.wyldcard.importer;

import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.builder.BackgroundModelBuilder;
import com.defano.wyldcard.parts.builder.ButtonModelBuilder;
import com.defano.wyldcard.parts.builder.CardModelBuilder;
import com.defano.wyldcard.parts.builder.StackModelBuilder;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.PartOwner;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.block.BackgroundBlock;
import com.defano.wyldcard.stackreader.block.CardBlock;
import com.defano.wyldcard.stackreader.block.StackBlock;
import com.defano.wyldcard.stackreader.enums.LayerFlag;
import com.defano.wyldcard.stackreader.enums.PartType;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import com.defano.wyldcard.stackreader.record.PartRecord;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class HyperCardStackImporter {

    public static void main(String[] v) throws HtException {
//        try {
//            HyperCardStackImporter.importStack(new File("/Users/matt/Dropbox/Practice"));
//        } catch (FileNotFoundException e) {
//            throw new HtException("Unable to open file for importing.");
//        }
    }

    public static void importStack(ExecutionContext context) {

        File stackFile = findFile(context);

        try {
            HyperCardStack hcStack = HyperCardStack.fromFile(stackFile, null);
            StackModel model = buildStack(stackFile.getName(), hcStack);
            WyldCard.getInstance().getStackManager().openStack(context, model, true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ImportException e) {
            e.printStackTrace();
        }

    }

    private static File findFile(ExecutionContext context) {
        FileDialog fd = new FileDialog(WyldCard.getInstance().getWindowManager().getWindowForStack(context, context.getCurrentStack()).getWindow(), "Choose stack to import", FileDialog.LOAD);
        fd.setMultipleMode(false);
        fd.setVisible(true);

        if (fd.getFiles().length > 0) {
            return fd.getFiles()[0];
        }

        return null;
    }

    private static StackModel buildStack(String name, HyperCardStack hcStack) {

        StackBlock stackBlock = hcStack.getBlock(StackBlock.class);
        StackModel stackModel = new StackModelBuilder()
                .withName(name)
                .withWidth(stackBlock.getWidth())
                .withHeight(stackBlock.getHeight())
                .build();

        for (CardBlock cardBlock : hcStack.getCardBlocks()) {
            buildCard(cardBlock, stackModel);
        }

        return stackModel;
    }

    private static void buildCard(CardBlock cardBlock, StackModel stackModel) {

        CardModel cardModel = new CardModelBuilder(stackModel)
                .withId(cardBlock.getBlockId())
                .withBackgroundId(cardBlock.getBkgndId())
                .withName(cardBlock.getName())
                .withIsMarked(false) // TODO
                .withCantDelete(Arrays.stream(cardBlock.getFlags()).anyMatch(f -> f == LayerFlag.CANT_DELETE))
                .withDontSearch(Arrays.stream(cardBlock.getFlags()).anyMatch(f -> f == LayerFlag.DONT_SEARCH))
                .withShowPict(Arrays.stream(cardBlock.getFlags()).noneMatch(f -> f == LayerFlag.HIDE_PICTURE))
                .withImage(cardBlock.getImage())
                .withScript(cardBlock.getScript())
                .build();

        buildBackground(cardBlock.getBkgndBlock(), stackModel);

        for (PartRecord part : cardBlock.getParts()) {
            if (part.getPartType() == PartType.BUTTON) {
                buildButton(part, cardModel);
            } else {
                buildField(part, cardModel);
            }
        }

        stackModel.addCard(cardModel);
    }

    private static void buildBackground(BackgroundBlock backgroundBlock, StackModel stackModel) {
        int backgroundId = backgroundBlock.getBlockId();

        // Skip building background if it already exists
        if (stackModel.getBackground(backgroundId) != null) {
            return;
        }

        BackgroundModel backgroundModel = new BackgroundModelBuilder(stackModel)
                .withName(backgroundBlock.getName())
                .withId(backgroundId)
                .withCantDelete(Arrays.stream(backgroundBlock.getFlags()).anyMatch(f -> f == LayerFlag.CANT_DELETE))
                .withDontSearch(Arrays.stream(backgroundBlock.getFlags()).anyMatch(f -> f == LayerFlag.DONT_SEARCH))
                .withShowPict(Arrays.stream(backgroundBlock.getFlags()).noneMatch(f -> f == LayerFlag.HIDE_PICTURE))
                .withImage(backgroundBlock.getImage())
                .withScript(backgroundBlock.getScript())
                .build();

        for (PartRecord part : backgroundBlock.getParts()) {
            if (part.getPartType() == PartType.BUTTON) {
                buildButton(part, backgroundModel);
            } else {
                buildField(part, backgroundModel);
            }
        }

        stackModel.addBackground(backgroundModel);
    }

    private static void buildButton(PartRecord partRecord, PartOwner parent) {
        ButtonModel buttonModel = new ButtonModelBuilder(parent.getType().asOwner(), parent.getParentPartModel())
                .withTop(partRecord.getTop())
                .withLeft(partRecord.getLeft())
                .withWidth(partRecord.getRight() - partRecord.getLeft())
                .withHeight(partRecord.getBottom() - partRecord.getTop())
                .withName(partRecord.getName())
                .withId(partRecord.getPartId())
                .withStyle(partRecord.getStyle().name())
                .withIconId(partRecord.getIconId())
                .withScript(partRecord.getScript())
                .build();

        parent.addPartModel(buttonModel);
    }

    private static void buildField(PartRecord partRecord, PartOwner parent) {

    }

}
