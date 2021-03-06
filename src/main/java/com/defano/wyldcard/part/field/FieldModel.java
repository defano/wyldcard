package com.defano.wyldcard.part.field;

import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.PartIdSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.util.Range;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.font.TextStyleSpecifier;
import com.defano.wyldcard.part.card.CardLayerPartModel;
import com.defano.wyldcard.part.field.styles.HyperCardTextField;
import com.defano.wyldcard.part.finder.LayeredPartFinder;
import com.defano.wyldcard.part.model.LogicalLinkObserver;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.part.util.FieldUtilities;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;

/**
 * A data model representing a field. See {@link FieldPart} for the associated controller object. This model is a mess.
 * Just go with it...
 * <p>
 * First: HyperCard mixes rich text (as edited by the user in the view) with plaintext (as written or read via script).
 * To support this, the model persists the RTF rich text, but exposes a computed property ('text') that scripts can
 * read or write. When a script sets the field's text property, the model attempts to intelligently replace the
 * contents of the field with the new contents, keeping the existing style intact as best as possible. See
 * {@link #replaceText(ExecutionContext, String)} for details.
 * <p>
 * Second: Fields placed in the background layer can either share their contents across all cards in the background
 * or, each card may have its own text (while still sharing other properties like size, location and showLines). This
 * necessitates the {@link #sharedText} and {@link #unsharedText} properties. Foreground-layer fields always use the
 * {@link #sharedText} value. This same sharing behavior applies to auto-selection (list fields).
 * <p>
 * Third: TextAlign is a separate, managed property of the field and not of the document model because Java's
 * RTFEditorKit doesn't support saving text alignment. Ugh! That's okay though, because HyperCard supports only a
 * single alignment per field, which we can model as a standard read/writable property in the
 * {@link com.defano.wyldcard.property.PropertiesModel}.
 * <p>
 * Fourth: Changes to the field's DOM can originate from the UI (i.e., a user typing into the field) or from HyperTalk.
 * Because changes can originate in the view ({@link HyperCardTextField}, this
 * requires a bidirectional observation binding: the model must observe the view for changes, and the view must observe
 * the model for changes...
 * <p>
 * Four-and-a-half: Not only do the view and the model need to observe one another, but some aspects of the model (like
 * rich-text data and selection ranges) are not readily modeled in the DefaultPropertiesModel, so they require their own
 * observation API, {@link FieldModelObserver}.
 * <p>
 * Fifth: When dealing with background fields whose text is not shared across all cards, we have to know which card
 * we're dealing with in order to know which text is in scope for document operations. The model exposes a
 * thread-local {@link #setCurrentCardId(int)} property that determines which card is "active" for the purposes of
 * getting or setting text. To support remote field references in script (i.e., 'put "ugh" into bg fld 3 of the last
 * card'), we can't simply query HyperCard for the displayed card.
 */
public class FieldModel extends CardLayerPartModel implements AddressableSelection, SelectableTextModel {

    public static final String PROP_TEXT = "text";
    public static final String PROP_DONTWRAP = "dontwrap";
    public static final String PROP_LOCKTEXT = "locktext";
    public static final String PROP_SHOWLINES = "showlines";
    public static final String PROP_STYLE = "style";
    public static final String PROP_SHAREDTEXT = "sharedtext";
    public static final String PROP_WIDEMARGINS = "widemargins";
    public static final String PROP_AUTOTAB = "autotab";
    public static final String PROP_AUTOSELECT = "autoselect";
    public static final String PROP_MULTIPLELINES = "multiplelines";
    public static final String PROP_SCROLLING = "scrolling";
    public static final String PROP_SCROLL = "scroll";
    public static final String PROP_DONTSEARCH = "dontsearch";

    // TODO: Not fully implemented
    public static final String PROP_TEXTHEIGHT = "textheight";
    public static final String PROP_FIXEDLINEHEIGHT = "fixedlineheight";

    private final Map<Integer, StyledDocument> unsharedText = new HashMap<>();
    private final Set<Integer> sharedAutoSelection = new HashSet<>();
    private final Map<Integer, Set<Integer>> unsharedAutoSelection = new HashMap<>();
    private StyledDocument sharedText;

    private transient FieldModelObserver observer;
    private transient Range selection;

    public FieldModel(Owner owner, PartModel parentPartModel) {
        super(PartType.FIELD, owner, parentPartModel);

        this.setCurrentCardId(parentPartModel.getId());

        define(PROP_SCRIPT).asValue();
        define(PROP_ID).asConstant(new Value());
        define(PROP_NAME).asValue();
        define(PROP_LEFT).asValue();
        define(PROP_TOP).asValue();
        define(PROP_WIDTH).asValue();
        define(PROP_HEIGHT).asValue();
        define(PROP_DONTWRAP).asValue(false);
        define(PROP_VISIBLE).asValue(true);
        define(PROP_LOCKTEXT).asValue(false);
        define(PROP_SHOWLINES).asValue(true);
        define(PROP_STYLE).asValue(FieldStyle.TRANSPARENT.getName());
        define(PROP_CONTENTS).asValue();
        define(PROP_SHAREDTEXT).asValue(false);
        define(PROP_WIDEMARGINS).asValue(false);
        define(PROP_AUTOTAB).asValue(false);
        define(PROP_AUTOSELECT).asValue(false);
        define(PROP_MULTIPLELINES).asValue(false);
        define(PROP_SCROLLING).asValue(false);
        define(PROP_SCROLL).asValue(0);
        define(PROP_DONTSEARCH).asValue(false);
        define(PROP_FIXEDLINEHEIGHT).asValue(false);

        define(PROP_TEXTFONT).asValue("Geneva");
        define(PROP_TEXTSIZE).asValue(12);
        define(PROP_TEXTSTYLE).asValue("plain");
        define(PROP_TEXTALIGN).asValue("left");

        this.postConstructFieldModel();
    }

    /**
     * {@inheritDoc}
     */
    @PostConstruct
    public void postConstructFieldModel() {
        super.postConstructCardLayerPartModel();

        define(PROP_NUMBER).asComputedReadOnlyValue((context, model) -> new Value(((LayeredPartFinder) ((FieldModel) model).getParentPartModel()).getPartNumber(context, (FieldModel) model, PartType.FIELD)));

        define(PROP_TEXT).asComputedValue()
                .withGetter((context, model) -> new Value(getText(context)))
                .withSetter((context, model, value) -> replaceText(context, value.toString()));

        define(PROP_TEXTHEIGHT).asComputedReadOnlyValue((context, model) -> new Value(model.get(context, PROP_TEXTSIZE).integerValue() * 1.33));
        define(PROP_SELECTEDTEXT).asComputedReadOnlyValue((context, model) -> getSelectedText(context));
        define(PROP_SELECTEDCHUNK).asComputedReadOnlyValue((context, model) -> getSelectedChunkExpression(context));
        define(PROP_SELECTEDLINE).asComputedReadOnlyValue((context, model) -> getSelectedLineExpression(context));

        addPropertyChangedObserver(LogicalLinkObserver.setOnSet(PROP_AUTOSELECT, PROP_DONTWRAP));
        addPropertyChangedObserver(LogicalLinkObserver.setOnSet(PROP_AUTOSELECT, PROP_LOCKTEXT));
    }

    /**
     * Sets the observer of scripted changes to the field's document model. Only a single observer is supported (and
     * should always be the field view object, {@link HyperCardTextField}.
     * <p>
     * In most every other case, a special observer interface is not required because observable attributes are modeled
     * by {@link com.defano.wyldcard.property.PropertiesModel}. Unfortunately, this technique requires properties
     * to be modeled as a HyperTalk {@link Value}. Coercing a byte array into and out of a Value would be ugly.
     *
     * @param observer The observer of DOM changes.
     */
    public void setDocumentObserver(FieldModelObserver observer) {
        this.observer = observer;
    }

    /**
     * Gets a Swing {@link StyledDocument} representing the rich text displayed in this field.
     *
     * @param context The execution context.
     * @return A StyledDocument representation of the contents of this field.
     */
    public StyledDocument getStyledDocument(ExecutionContext context) {
        return getStyledDocument(context, getCurrentCardId(context));
    }

    private StyledDocument getStyledDocument(ExecutionContext context, int forCardId) {
        if (isSharedText(context)) {
            return getSharedText(context);
        } else {
            return getUnsharedText(context, forCardId);
        }
    }

    private StyledDocument getUnsharedText(ExecutionContext context, int cardId) {
        if (!unsharedText.containsKey(cardId)) {
            unsharedText.put(cardId, getNewDocument(context));
        }

        return unsharedText.get(cardId);
    }

    private StyledDocument getSharedText(ExecutionContext context) {
        if (sharedText == null) {
            sharedText = getNewDocument(context);
        }

        return sharedText;
    }

    private StyledDocument getNewDocument(ExecutionContext context) {
        Style as = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(as, get(context, FieldModel.PROP_TEXTFONT).toString());
        StyleConstants.setFontSize(as, get(context, FieldModel.PROP_TEXTSIZE).integerValue());

        StyleContext sc = new StyleContext();
        sc.addStyle(StyleContext.DEFAULT_STYLE, as);

        DefaultStyledDocument doc = new DefaultStyledDocument(sc);
        doc.setCharacterAttributes(0, 1, as, true);
        doc.setParagraphAttributes(0, 1, as, true);
        return doc;
    }

    /**
     * Persists the given StyledDocument data into this model. Affects either the shared document data, or the unshared
     * data depending on whether the field is in the background and has the sharedText property.
     *
     * @param context The execution context.
     * @param doc     The styled document data to persist into the model.
     */
    public void setStyledDocument(ExecutionContext context, StyledDocument doc) {
        if (isSharedText(context)) {
            sharedText = doc;
        } else {
            unsharedText.put(getCurrentCardId(context), doc);
        }
    }

    /**
     * Determine if the model should use the sharedText document data.
     *
     * @param context The execution context.
     * @return True if the model should use sharedText data; false otherwise.
     */
    private boolean isSharedText(ExecutionContext context) {
        return getOwner() == Owner.CARD || get(context, PROP_SHAREDTEXT).booleanValue();
    }

    /**
     * Gets a plaintext representation of the text held in this model.
     *
     * @param context The execution context.
     * @return A plaintext representation of the contents of this field.
     */
    @Override
    public String getText(ExecutionContext context) {
        return getText(context, getCurrentCardId(context));
    }

    public String getText(ExecutionContext context, int forCardId) {
        StyledDocument doc = getStyledDocument(context, forCardId);
        try {
            return doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }

    public void applyFont(ExecutionContext context, int forCardId, int start, String fontFamily) {
        StyledDocument doc = getStyledDocument(context, forCardId);
        doc.setCharacterAttributes(start, doc.getLength() - start, TextStyleSpecifier.fromFontFamily(fontFamily).toAttributeSet(), false);
    }

    public void applyFontSize(ExecutionContext context, int forCardId, int start, int fontSize) {
        StyledDocument doc = getStyledDocument(context, forCardId);
        doc.setCharacterAttributes(start, doc.getLength() - start, TextStyleSpecifier.fromFontSize(fontSize).toAttributeSet(), false);
    }

    public void applyFontStyle(ExecutionContext context, int forCardId, int start, Value style) {
        StyledDocument doc = getStyledDocument(context, forCardId);
        doc.setCharacterAttributes(start, doc.getLength() - start, TextStyleSpecifier.fromFontStyle(style).toAttributeSet(), false);
    }

    /**
     * Replaces the field's text with the given String value, attempting as best as possible to intelligently
     * maintain the field's existing style.
     * <p>
     * It is not possible to correctly restyle the new text in every case. This is a result of the {@link FieldModel}
     * not being able to notify us of insert/delete operations.
     * <p>
     * This method invokes Google's DiffMatchPatch utility to generate a change set, then attempts to apply each change
     * independently to let the {@link StyledDocument} model best preserve its formatting.
     * <p>
     *
     * @param context The execution context.
     * @param newText The text with which to replace the field's existing contents.
     */
    private void replaceText(ExecutionContext context, String newText) {
        String existingText = getText(context);

        // Don't waste our own time
        if (newText.equals(existingText)) {
            return;
        }

        int changePosition = 0;
        StyledDocument document = getStyledDocument(context);

        AttributeSet style = document.getLength() == 0 ?
                WyldCard.getInstance().getFontManager().getFocusedTextStyle().toAttributeSet() :
                document.getCharacterElement(0).getAttributes();

        try {
            for (DiffMatchPatch.Diff thisDiff : getTextDifferences(existingText, newText)) {
                switch (thisDiff.operation) {
                    case EQUAL:
                        style = document.getCharacterElement(changePosition).getAttributes();
                        changePosition += thisDiff.text.length();
                        break;
                    case DELETE:
                        style = document.getCharacterElement(changePosition).getAttributes();
                        document.remove(changePosition, thisDiff.text.length());
                        break;
                    case INSERT:
                        document.insertString(changePosition, thisDiff.text, style);
                        changePosition += thisDiff.text.length();
                        break;
                }
            }
        } catch (BadLocationException e) {
            throw new RuntimeException("An error occurred updating field text.", e);
        }

        setStyledDocument(context, document);              // Save our changes
        fireDocumentChangeObserver(context, document);     // ... and let the view know know about 'em
    }

    /**
     * Produces a set of differences between the existing and replacement strings.
     *
     * @param existing    The existing text to analyze
     * @param replacement The new/replacement text to compare
     * @return A list of differences
     */
    private LinkedList<DiffMatchPatch.Diff> getTextDifferences(String existing, String replacement) {
        DiffMatchPatch dmp = new DiffMatchPatch();

        LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(existing, replacement);
        dmp.diffCleanupSemantic(diffs);

        return diffs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextStyle(ExecutionContext context, TextStyleSpecifier tss) {
        StyledDocument doc = getStyledDocument(context);
        doc.setCharacterAttributes(0, doc.getLength() + 1, tss.toAttributeSet(), true);

        setStyledDocument(context, doc);
        fireDocumentChangeObserver(context, doc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextStyleSpecifier getTextStyle(ExecutionContext context) {
        return TextStyleSpecifier.fromAttributeSet(getStyledDocument(context).getCharacterElement(0).getAttributes());
    }

    /**
     * Sets the font family of the indicated range of characters in this field; has no effect if the font family
     * is not available on this system.
     *
     * @param context       The execution context.
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontFamily    The new font family to apply.
     */
    public void setTextFontFamily(ExecutionContext context, int startPosition, int length, Value fontFamily) {
        StyledDocument doc = getStyledDocument(context);

        // Special case; zero-length document does not persist prev style, replace with focused style
        if (doc.getLength() == 0) {
            TextStyleSpecifier tss = WyldCard.getInstance().getFontManager().getFocusedTextStyle();
            tss.setFontFamily(fontFamily.toString());
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), true);
        }

        // Apply font family to document
        else {
            TextStyleSpecifier tss = TextStyleSpecifier.fromFontFamily(fontFamily.toString());
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), false);
        }

        setStyledDocument(context, doc);
        fireDocumentChangeObserver(context, doc);
    }

    /**
     * Sets the font size (in points) of the indicated range of characters in this field.
     *
     * @param context       The execution context.
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontSize      The new font size to apply.
     */
    public void setTextFontSize(ExecutionContext context, int startPosition, int length, Value fontSize) {
        StyledDocument doc = getStyledDocument(context);

        // Special case; zero-length document does not persist prev style, replace with focused style
        if (doc.getLength() == 0) {
            TextStyleSpecifier tss = WyldCard.getInstance().getFontManager().getFocusedTextStyle();
            tss.setFontSize(fontSize.integerValue());
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), true);
        }

        // Apply font size to document
        else {
            TextStyleSpecifier tss = TextStyleSpecifier.fromFontSize(fontSize.integerValue());
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), false);
        }

        setStyledDocument(context, doc);
        fireDocumentChangeObserver(context, doc);
    }

    /**
     * Sets the font style of the indicated range of characters in this field; style should be 'italic', 'bold',
     * 'bold,italic' or 'plain'.
     *
     * @param context       The execution context.
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontStyle     The new font style to apply.
     */
    public void setTextFontStyle(ExecutionContext context, int startPosition, int length, Value fontStyle) {
        StyledDocument doc = getStyledDocument(context);

        // Special case; zero-length document does not persist prev style, replace with focused style
        if (doc.getLength() == 0) {
            TextStyleSpecifier tss = WyldCard.getInstance().getFontManager().getFocusedTextStyle();
            tss.setFontStyle(fontStyle);
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), true);
        }

        // Apply style changes to each character
        else {
            for (int index = startPosition; index < startPosition + length; index++) {
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(getStyledDocument(context).getCharacterElement(index).getAttributes());
                tss.setFontStyle(fontStyle);
                doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
            }
        }

        setStyledDocument(context, doc);
        fireDocumentChangeObserver(context, doc);
    }

    /**
     * Gets the font family of the indicated range of characters in the field, or 'mixed' if multiple fonts are present
     * in the range.
     *
     * @param context       The execution context.
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @return The name of the font family present in the range of characters or 'mixed' if there are
     * multiple fonts
     */
    public Value getTextFontFamily(ExecutionContext context, int startPosition, int length) {
        for (int index = startPosition; index < startPosition + length - 1; index++) {
            if (!getTextFontFamily(context, index).equals(getTextFontFamily(context, index + 1))) {
                return new Value("mixed");
            }
        }
        return new Value(getTextFontFamily(context, startPosition));
    }

    private Value getTextFontFamily(ExecutionContext context, int position) {
        return new Value(getStyledDocument(context).getCharacterElement(position).getAttributes().getAttribute(StyleConstants.FontFamily));
    }

    /**
     * Gets the font size of the indicated range of characters in the field, or 'mixed' if multiple sizes are present
     * in the range.
     *
     * @param context       The execution context.
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @return The size of the font present in the range of characters or 'mixed' if there are multiple
     * sizes.
     */
    public Value getTextFontSize(ExecutionContext context, int startPosition, int length) {
        for (int index = startPosition; index < startPosition + length - 1; index++) {
            if (!getTextFontSize(context, index).equals(getTextFontSize(context, index + 1))) {
                return new Value("mixed");
            }
        }
        return new Value(getTextFontSize(context, startPosition));
    }

    private Value getTextFontSize(ExecutionContext context, int position) {
        return new Value(getStyledDocument(context).getCharacterElement(position).getAttributes().getAttribute(StyleConstants.FontSize));
    }

    /**
     * Gets the font style of the indicated range of characters in the field, or 'mixed' if multiple styles are present
     * in the range.
     *
     * @param context       The execution context.
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @return The font style present in the range of characters or 'mixed' if there are multiple styles
     */
    public Value getTextFontStyle(ExecutionContext context, int startPosition, int length) {
        for (int index = startPosition; index < startPosition + length - 1; index++) {
            if (!getTextFontStyle(context, index).equals(getTextFontStyle(context, index + 1))) {
                return new Value("mixed");
            }
        }
        return new Value(getTextFontStyle(context, startPosition));
    }

    private Value getTextFontStyle(ExecutionContext context, int position) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(getStyledDocument(context).getCharacterElement(position).getAttributes());
        return tss.getHyperTalkStyle();
    }

    /**
     * Auto-selects the given line number. Auto-selecting the line highlights the entire width of the line and places
     * the contents of the line into the selection, Has no effect if the field has no such line.
     *
     * @param context    The execution context.
     * @param lineNumber The line number to auto-select
     */
    public void autoSelectLine(ExecutionContext context, int lineNumber, boolean appendSelection) {
        if (lineNumber >= 1 && lineNumber <= new Value(getText(context)).getLines(context).size()) {

            if (!appendSelection) {
                getAutoSelectedLines(context).clear();
            }

            getAutoSelectedLines(context).add(lineNumber);
            fireAutoSelectChangeObserver(context, getAutoSelectedLines(context));
        }
    }

    /**
     * Auto-selects the given range of lines.
     *
     * @param context   The execution context.
     * @param startLine The first line in the auto-selection, counting from 1, inclusive.
     * @param endLine   The last line in the auto-selection, counting from 1, inclusive.
     */
    private void autoSelectLines(ExecutionContext context, int startLine, int endLine) {
        Set<Integer> autoSelection = getAutoSelectedLines(context);

        autoSelection.clear();
        for (int line = startLine; line <= endLine; line++) {
            autoSelection.add(line);
        }

        fireAutoSelectChangeObserver(context, autoSelection);
    }

    /**
     * Returns the set of lines currently that are currently auto-selected, or an empty set if auto-selection is
     * disabled.
     *
     * @param context The execution context.
     * @return The auto-selected lines.
     */
    public Set<Integer> getAutoSelectedLines(ExecutionContext context) {
        if (isAutoSelection(context)) {
            if (isSharedText(context)) {
                return sharedAutoSelection;
            } else {
                return unsharedAutoSelection.computeIfAbsent(getCurrentCardId(context), k -> new HashSet<>());
            }
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Returns the range of characters in the field document represented by the auto-selection, or an empty range
     * if auto-selection is disabled.
     *
     * @param context The execution context.
     * @return The range of characters in the auto selection.
     */
    private Range getAutoSelectionRange(ExecutionContext context) {
        if (isAutoSelection(context)) {
            int[] lines = getAutoSelectedLines(context).stream().mapToInt(Number::intValue).toArray();
            return FieldUtilities.getLinesRange(getText(context), lines);
        } else {
            return new Range();
        }
    }

    /**
     * Determines if the auto-selection property is enabled on this field.
     *
     * @param context The execution context.
     * @return True if auto-selection is enabled; false otherwise
     */
    public boolean isAutoSelection(ExecutionContext context) {
        return get(context, FieldModel.PROP_AUTOSELECT).booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelection(ExecutionContext context, Range selection) {
        if (isAutoSelection(context)) {
            autoSelectLines(context, FieldUtilities.getLineOfChar(selection.start, getText(context)), FieldUtilities.getLineOfChar(selection.end, getText(context)));
        } else {
            this.selection = selection;
            fireSelectionChange(context, selection);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param context The execution context.
     */
    @Override
    public Range getSelection(ExecutionContext context) {
        if (isAutoSelection(context)) {
            return getAutoSelectionRange(context);
        } else {
            return this.selection == null ? new Range() : this.selection;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewDidUpdateSelection(Range selection) {
        this.selection = selection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueProperty() {
        return PROP_TEXT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        setParentPartModel(parentPartModel);
    }

    /**
     * {@inheritDoc}
     *
     * @param context The execution context.
     */
    @Override
    public PartSpecifier getPartSpecifier(ExecutionContext context) {
        return new PartIdSpecifier(getOwner(), PartType.FIELD, getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldModel getSelectableTextModel() {
        return this;
    }

    private void fireAutoSelectChangeObserver(ExecutionContext context, Set<Integer> selectedLines) {
        if (observer != null && getCurrentCardIdOrNull() == context.getCurrentCard().getId(context)) {
            SwingUtilities.invokeLater(() -> observer.onAutoSelectionChanged(selectedLines));
        }
    }

    private void fireDocumentChangeObserver(ExecutionContext context, StyledDocument document) {
        if (observer != null && getCurrentCardIdOrNull() == context.getCurrentCard().getId(context)) {
            SwingUtilities.invokeLater(() -> observer.onStyledDocumentChanged(document));
        }
    }

    private void fireSelectionChange(ExecutionContext context, Range selection) {
        // Update 'the selection' HyperCard property
        WyldCard.getInstance().getSelectionManager().setSelection(getPartSpecifier(context), selection);

        if (observer != null && getCurrentCardIdOrNull() == context.getCurrentCard().getId(context)) {
            SwingUtilities.invokeLater(() -> observer.onSelectionChange(selection));
        }
    }

}

