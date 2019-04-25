package com.defano.wyldcard.importer;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.builder.*;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardLayer;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.block.*;
import com.defano.wyldcard.stackreader.enums.*;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.record.*;
import com.defano.wyldcard.thread.Invoke;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class StackFormatConverter {

    private ConversionStatusObserver status;
    private ConversionProgressObserver progress;

    private StackFormatConverter(ConversionStatusObserver status, ConversionProgressObserver progress) {
        this.status = status;
        this.progress = progress;
    }

    public static void startConversion(File stackFile, ConversionStatusObserver status, ConversionProgressObserver progress) {
        if (progress == null || status == null) {
            throw new IllegalArgumentException("Conversion observer cannot be null.");
        }

        StackFormatConverter importer = new StackFormatConverter(status, progress);
//        importer.convert(stackFile);
        Invoke.asynchronouslyOnWorkerThread(() -> importer.convert(stackFile));
    }

    private void convert(File stackFile) {

        try {
            HyperCardStack hcStack = HyperCardStack.fromFile(stackFile, null);
            StackModel model = buildStack(new ExecutionContext(), stackFile.getName(), hcStack);
            status.onConversionSucceeded(model);

        } catch (FileNotFoundException e) {
            status.onConversionFailed("Cannot find or open file " + stackFile.getAbsolutePath(), e);
        } catch (ImportException e) {
            status.onConversionFailed("File is not a HyperCard stack or the stack file is corrupted.", e);
        } catch (Exception t) {
            status.onConversionFailed("An unexpected error occurred while reading the stack file.", t);
        }
    }

    private StackModel buildStack(ExecutionContext context, String name, HyperCardStack hcStack) {

        List<CardBlock> cardBlocks = hcStack.getCardBlocks();

        StackBlock stackBlock = hcStack.getBlock(StackBlock.class);
        StackModel stackModel = new StackModelBuilder()
                .withName(name)
                .withWidth(stackBlock.getWidth())
                .withHeight(stackBlock.getHeight())
                .withScript(stackBlock.getStackScript())
                .build();

        for (int cardIdx = 0; cardIdx < cardBlocks.size(); cardIdx++) {
            buildCard(context, cardBlocks.get(cardIdx), stackModel);
            progress.onConversionProgressUpdate(cardIdx + 1, cardBlocks.size(), "Imported card " + (cardIdx + 1) + " of " + cardBlocks.size());
        }

        return stackModel;
    }

    private void buildCard(ExecutionContext context, CardBlock cardBlock, StackModel stackModel) {

        // Create card
        CardModel cardModel = new CardModelBuilder(stackModel)
                .withId(cardBlock.getBlockId())
                .withBackgroundId(cardBlock.getBkgndId())
                .withName(cardBlock.getName())
                .withIsMarked(isCardMarked(cardBlock.getBlockId(), cardBlock.getStack()))
                .withCantDelete(Arrays.stream(cardBlock.getFlags()).anyMatch(f -> f == LayerFlag.CANT_DELETE))
                .withDontSearch(Arrays.stream(cardBlock.getFlags()).anyMatch(f -> f == LayerFlag.DONT_SEARCH))
                .withShowPict(Arrays.stream(cardBlock.getFlags()).noneMatch(f -> f == LayerFlag.HIDE_PICTURE))
                .withImage(cardBlock.getImage())
                .withScript(cardBlock.getScript())
                .build();

        // Create background (if does not exist)
        buildBackground(cardBlock.getBkgndBlock(), stackModel);

        // Create all buttons and fields and on this card
        buildParts(cardBlock.getParts(), cardModel, cardBlock);

        // Set card-contextual properties (unshared field text, button hilite, etc.)
        for (PartContentRecord pcr : cardBlock.getContents()) {
            applyUnsharedButtonHilite(context, pcr, cardModel);
            applyTextContents(context, pcr, cardModel, false);
            applyTextStyles(context, pcr, cardModel, cardBlock, false);
        }

        // Set background-contextual properties (shared text styles)
        for (PartContentRecord pcr : cardBlock.getBkgndBlock().getContents()) {
            applyTextContents(context, pcr, cardModel, true);
            applyTextStyles(context, pcr, cardModel, cardBlock, true);
        }

        stackModel.addCard(cardModel);
    }

    /**
     * Apply text style data from a given {@link PartContentRecord} to a field on a given card (or its background).
     *
     * @param context The execution context
     * @param pcr The part content record providing style data
     * @param cardModel The model of the card that contains the field (or the background field)
     * @param cardBlock The HyperCard stack's {@link CardBlock} or {@link BackgroundBlock}
     * @param sharedText When true, style applies to shared text, when false, style applies to unshared text.
     */
    private void applyTextStyles(ExecutionContext context, PartContentRecord pcr, CardModel cardModel, CardLayerBlock cardBlock, boolean sharedText) {
        FieldModel field;

        if (pcr.isBackgroundPart()) {
            field = cardModel.getBackgroundModel().getFieldModels().stream()
                    .filter(f -> f.getId() == pcr.getRawPartId())
                    .findFirst()
                    .orElse(null);
        } else {
            field = cardModel.getFieldModels().stream()
                    .filter(f -> f.getId() == -pcr.getRawPartId())
                    .findFirst()
                    .orElse(null);
        }

        if (field != null && (!sharedText || field.get(context, FieldModel.PROP_SHAREDTEXT).booleanValue())) {
            int cardId = cardModel.getId();

            if (pcr.isPlaintext()) {
                field.applyFont(context, cardId, 0, field.get(context, FieldModel.PROP_TEXTFONT).toString());
                field.applyFontSize(context, cardId, 0, field.get(context, FieldModel.PROP_TEXTSIZE).integerValue());
                field.applyFontStyle(context, cardId, 0, field.get(context, FieldModel.PROP_TEXTSTYLE));
            } else {
                applyStyleSpans(context, field, cardModel.getId(), cardBlock.getStack(), pcr.getStyleSpans());
            }
        }
    }

    private void applyTextContents(ExecutionContext context, PartContentRecord pcr, CardModel cardModel, boolean sharedText) {
        if (pcr.isBackgroundPart()) {
            FieldModel field = cardModel.getBackgroundModel().getField(pcr.getRawPartId());

            // Set un-shared text value
            if (field != null && (!sharedText || field.get(context, FieldModel.PROP_SHAREDTEXT).booleanValue())) {
                field.setCurrentCardId(cardModel.getId());
                field.set(context, FieldModel.PROP_TEXT, new Value(pcr.getText()));
            }
        }
    }

    /**
     * Sets the hilite property of background buttons. Has no effect when invoked with a PartContentRecord that does
     * not refer to a button or if the button does not exist on the card's background.
     * <p>
     * Background buttons may choose not to "share" their hilite state across cards. This allows, for example, a
     * background layer checkbox to have different checked/unchecked values on each card in the background. This method
     * applies the card-specific hilite value to such a button.
     *
     * @param pcr       The button's PartContentRecord.
     * @param cardModel The model of the card whose hilite is being adjusted.
     */
    private void applyUnsharedButtonHilite(ExecutionContext context, PartContentRecord pcr, CardModel cardModel) {
        if (pcr.isBackgroundPart()) {   // Only applies to background buttons
            ButtonModel bm = cardModel.getBackgroundModel().getButton(pcr.getRawPartId());

            if (bm != null) {
                bm.setCurrentCardId(cardModel.getId());
                bm.set(context, ButtonModel.PROP_HILITE, new Value(pcr.isBkgndButtonHilited()));
            }
        }
    }

    private void buildBackground(BackgroundBlock backgroundBlock, StackModel stackModel) {
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
        buildParts(backgroundBlock.getParts(), backgroundModel, backgroundBlock);

        stackModel.addBackground(backgroundModel);
    }

    private void buildParts(PartRecord[] parts, CardLayer parent, CardLayerBlock block) {
        for (int partNumber = 0; partNumber < parts.length; partNumber++) {
            PartRecord partRecord = parts[partNumber];
            if (partRecord.getPartType() == PartType.BUTTON) {
                buildButton(partRecord, partNumber, parent, block);
            } else {
                buildField(partRecord, partNumber, parent, block);
            }
        }
    }

    private void buildButton(PartRecord partRecord, int partNumber, CardLayer parent, CardLayerBlock block) {

        ButtonModel buttonModel = new ButtonModelBuilder(parent.getType().asOwner(), parent.getParentPartModel())
                .withPartNumber(partNumber)
                .withTop(partRecord.getTop())
                .withLeft(partRecord.getLeft())
                .withWidth(partRecord.getRight() - partRecord.getLeft())
                .withHeight(partRecord.getBottom() - partRecord.getTop())
                .withName(partRecord.getName())
                .withId(partRecord.getPartId())
                .withPartStyle(partRecord.getStyle().hypertalkName())
                .withFamily(partRecord.getFamily())
                .withTextSize(partRecord.getTextSize())
                .withTextFont(block.getStack().getBlock(FontTableBlock.class).getFont(partRecord.getTextFontId()).getFontName())
                .withTextStyle(FontStyle.asHypertalkList(partRecord.getFontStyles()))
                .withTextAlign(partRecord.getTextAlign().name())
                .withIconId(partRecord.getIconId())
                .withScript(partRecord.getScript())
                .withContents(block.getPartContents(partRecord.getPartId()).getText())
                .withSelectedItem(partRecord.getFirstSelectedLine())
                .withShowName(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.SHOW_NAME))
                .withIsEnabled(Arrays.stream(partRecord.getFlags()).noneMatch(f -> f == PartFlag.DISABLED))
                .withAutoHilite(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.AUTO_HILITE))
                .withHilite(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.HILITE))
                .withSharedHilite(Arrays.stream(partRecord.getExtendedFlags()).noneMatch(f -> f == ExtendedPartFlag.NO_SHARING_HILITE))
                .withIsVisible(Arrays.stream(partRecord.getFlags()).noneMatch(f -> f == PartFlag.HIDDEN))
                .build();

        parent.addPartModel(buttonModel);
    }

    private void buildField(PartRecord partRecord, int partNumber, CardLayer parent, CardLayerBlock block) {

        FieldModel fieldModel = new FieldModelBuilder(parent.getType().asOwner(), parent.getParentPartModel())
                .withPartNumber(partNumber)
                .withTop(partRecord.getTop())
                .withLeft(partRecord.getLeft())
                .withWidth(partRecord.getRight() - partRecord.getLeft())
                .withHeight(partRecord.getBottom() - partRecord.getTop())
                .withName(partRecord.getName())
                .withId(partRecord.getPartId())
                .withPartStyle(partRecord.getStyle().name())
                .withIsVisible(Arrays.stream(partRecord.getFlags()).noneMatch(f -> f == PartFlag.HIDDEN))
                .withDontWrap(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.DONT_WRAP))
                .withDontSearch(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.DONT_SEARCH))
                .withSharedText(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.SHARED_TEXT))
                .withTextSize(partRecord.getTextSize())
                .withTextFont(block.getStack().getBlock(FontTableBlock.class).getFont(partRecord.getTextFontId()).getFontName())
                .withTextStyle(FontStyle.asHypertalkList(partRecord.getFontStyles()))
                .withTextAlign(partRecord.getTextAlign().name())
                .withText(block.getPartContents(partRecord.getPartId()).getText())
                .withAutoTab(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.AUTO_TAB))
                .withLockText(Arrays.stream(partRecord.getFlags()).anyMatch(f -> f == PartFlag.LOCK_TEXT))
                .withAutoSelect(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.AUTO_SELECT))
                .withShowLines(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.SHOW_LINES))
                .withWideMargins(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.WIDE_MARGINS))
                .withMultipleLines(Arrays.stream(partRecord.getExtendedFlags()).anyMatch(f -> f == ExtendedPartFlag.MULTIPLE_LINES))
                .withScript(partRecord.getScript())
                .build();

        parent.addPartModel(fieldModel);
    }

    private void applyStyleSpans(ExecutionContext context, FieldModel fieldModel, int cardId, HyperCardStack stack, StyleSpanRecord[] styleSpans) {

        FontTableBlock fontTableBlock = stack.getBlock(FontTableBlock.class);
        StyleTableBlock styleTableBlock = stack.getBlock(StyleTableBlock.class);

        for (StyleSpanRecord record : styleSpans) {
            StyleRecord style = styleTableBlock.getStyle(record.getStyleId());
            int position = record.getTextPosition();

            if (style.getFontId() != -1) {
                fieldModel.applyFont(context, cardId, position, fontTableBlock.getFont(style.getFontId()).getFontName());
            } else {
                fieldModel.applyFont(context, cardId, position, fieldModel.get(context, FieldModel.PROP_TEXTFONT).toString());
            }

            if (style.getFontSize() != -1) {
                fieldModel.applyFontSize(context, cardId, position, (int) style.getFontSize());
            } else {
                fieldModel.applyFontSize(context, cardId, position, fieldModel.get(context, FieldModel.PROP_TEXTSIZE).integerValue());
            }

            String styleString = FontStyle.asHypertalkList(style.getStyles());
            fieldModel.applyFontStyle(context, cardId, position, new Value(styleString));
        }
    }

    private boolean isCardMarked(int cardId, HyperCardStack stack) {
        for (PageBlock thisPage : stack.getBlocks(PageBlock.class)) {
            for (PageEntryRecord thisEntry : thisPage.getPageEntries()) {
                if (thisEntry.getCardId() == cardId) {
                    return Arrays.stream(thisEntry.getFlags()).anyMatch(f -> f == PageFlag.MARKED_CARD);
                }
            }
        }

        throw new IllegalStateException("Can't find card in page entry");
    }

}
