package com.defano.wyldcard.importer;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.fonts.TextStyleSpecifier;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.builder.*;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.PartOwner;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.block.*;
import com.defano.wyldcard.stackreader.enums.ExtendedPartFlag;
import com.defano.wyldcard.stackreader.enums.LayerFlag;
import com.defano.wyldcard.stackreader.enums.PartFlag;
import com.defano.wyldcard.stackreader.enums.PartType;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.record.PartContentRecord;
import com.defano.wyldcard.stackreader.record.PartRecord;
import com.defano.wyldcard.stackreader.record.StyleRecord;
import com.defano.wyldcard.stackreader.record.StyleSpanRecord;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class HyperCardStackImporter {

    public static void importStack(ExecutionContext context) {

        File stackFile = findFile(context);

        try {
            HyperCardStack hcStack = HyperCardStack.fromFile(stackFile, null);
            StackModel model = buildStack(stackFile.getName(), hcStack);
            WyldCard.getInstance().getStackManager().openStack(context, model, true);

        } catch (FileNotFoundException | ImportException e) {
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
                .withScript(stackBlock.getStackScript())
                .build();

        for (CardBlock cardBlock : hcStack.getCardBlocks()) {
            buildCard(cardBlock, stackModel);
        }

        return stackModel;
    }

    private static void buildCard(CardBlock cardBlock, StackModel stackModel) {

        // Create card
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

        // Create background (if does not exist)
        buildBackground(cardBlock.getBkgndBlock(), stackModel);

        // Create all buttons and fields
        Arrays.stream(cardBlock.getParts()).forEach(p -> buildPart(p, cardModel, cardBlock));

        // Set unshared text on background fields
        for (PartContentRecord pcr : cardBlock.getContents()) {
            if (pcr.getPartId() >= 0) {
                FieldModel fm = cardModel.getBackgroundModel().getField(pcr.getPartId());

                if (fm != null) {
                    int cardId = cardModel.getId(new ExecutionContext());
                    fm.setCurrentCardId(cardId);
                    fm.setKnownProperty(new ExecutionContext(), FieldModel.PROP_TEXT, new Value(pcr.getText()));
                }
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

        // Create all buttons and fields
        Arrays.stream(backgroundBlock.getParts()).forEach(p -> buildPart(p, backgroundModel, backgroundBlock));
        stackModel.addBackground(backgroundModel);
    }

    private static void buildPart(PartRecord part, PartOwner parent, AbstractCardBlock block) {
        if (part.getPartType() == PartType.BUTTON) {
            buildButton(part, parent, block);
        } else {
            buildField(part, parent, block);
        }
    }

    private static void buildButton(PartRecord partRecord, PartOwner parent, AbstractCardBlock block) {

        ButtonModel buttonModel = new ButtonModelBuilder(parent.getType().asOwner(), parent.getParentPartModel())
                .withTop(partRecord.getTop())
                .withLeft(partRecord.getLeft())
                .withWidth(partRecord.getRight() - partRecord.getLeft())
                .withHeight(partRecord.getBottom() - partRecord.getTop())
                .withName(partRecord.getName())
                .withId(partRecord.getPartId())
                .withStyle(partRecord.getStyle().name())
                .withFamily(partRecord.getFamily())
                .withTextFont(block.getStack().getBlock(FontTableBlock.class).getFont(partRecord.getTextFontId()).getFontName())
                .withTextSize(partRecord.getTextSize())
                .withTextAlign(partRecord.getTextAlign().toString())
//                .withTextStyle(partRecord.getFontStyles())  // TODO
                .withIconId(partRecord.getIconId())
                .withScript(partRecord.getScript())
                .withShowName(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.SHOW_NAME))
                .withIsEnabled(Arrays.stream(partRecord.getFlags()).noneMatch(f -> f == PartFlag.DISABLED))
                .withAutoHilite(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.AUTO_HILITE))
                .withHilite(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.HILITE))
                .withIsVisible(Arrays.stream(partRecord.getFlags()).noneMatch(f -> f == PartFlag.HIDDEN))
                .build();

        parent.addPartModel(buttonModel);
    }

    private static void buildField(PartRecord partRecord, PartOwner parent, AbstractCardBlock block) {

        FieldModel fieldModel = new FieldModelBuilder(parent.getType().asOwner(), parent.getParentPartModel())
                .withTop(partRecord.getTop())
                .withLeft(partRecord.getLeft())
                .withWidth(partRecord.getRight() - partRecord.getLeft())
                .withHeight(partRecord.getBottom() - partRecord.getTop())
                .withName(partRecord.getName())
                .withId(partRecord.getPartId())
                .withStyle(partRecord.getStyle().name())
                .withIsHidden(Arrays.stream(partRecord.getFlags()).noneMatch(f -> f == PartFlag.HIDDEN))
                .withDontWrap(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.DONT_WRAP))
                .withDontSearch(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.DONT_SEARCH))
                .withSharedText(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.SHARED_TEXT))
                .withAutoTab(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.AUTO_TAB))
                .withLockText(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.LOCK_TEXT))
                .withAutoSelect(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.AUTO_SELECT))
                .withShowLines(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.SHOW_LINES))
                .withWideMargins(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.WIDE_MARGINS))
                .withMultipleLines(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.MULTIPLE_LINES))
                .withText(block.getPartContents(partRecord.getPartId()).getText())
                .withScript(partRecord.getScript())
                .build();

        fieldModel.setTextStyle(new ExecutionContext(), getTextStyleSpecifier(partRecord, block.getStack()));

        if (parent instanceof CardModel) {
            applyTextStyles(
                    fieldModel,
                    ((CardModel) parent).getId(new ExecutionContext()),
                    block.getStack(),
                    block.getPartContents(partRecord.getPartId()).getStyleSpans()
            );
        }

        parent.addPartModel(fieldModel);
    }

    private static TextStyleSpecifier getTextStyleSpecifier(PartRecord partRecord, HyperCardStack stack) {
        FontTableBlock fontTableBlock = stack.getBlock(FontTableBlock.class);

        return TextStyleSpecifier.fromAlignNameStyleSize(
                new Value(partRecord.getTextAlign().name()),
                new Value(fontTableBlock.getFont(partRecord.getTextFontId()).getFontName()),
                new Value(Arrays.toString(partRecord.getFontStyles())),
                new Value(partRecord.getTextSize())
        );
    }

    private static void applyTextStyles(FieldModel fieldModel, int cardId, HyperCardStack stack, StyleSpanRecord[] styleSpans) {

        ExecutionContext context = new ExecutionContext();

        FontTableBlock fontTableBlock = stack.getBlock(FontTableBlock.class);
        StyleTableBlock styleTableBlock = stack.getBlock(StyleTableBlock.class);

        for (StyleSpanRecord record : styleSpans) {
            StyleRecord style = styleTableBlock.getStyle(record.getStyleId());
            int position = record.getTextPosition();

            if (style.getFontId() != -1) {
                fieldModel.applyFont(context, cardId, position, fontTableBlock.getFont(style.getFontId()).getFontName());
            }

            if (style.getFontSize() != -1) {
                fieldModel.applyFontSize(context, cardId, position, (int) style.getFontSize());
            }

            if (style.getStyles() != null) {

            }
        }
    }

}
