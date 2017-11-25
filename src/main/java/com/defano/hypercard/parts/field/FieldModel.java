package com.defano.hypercard.parts.field;

import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.paint.FontContext;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.field.styles.HyperCardTextField;
import com.defano.hypercard.parts.model.LogicalLinkObserver;
import com.defano.hypercard.parts.util.FieldUtilities;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.utils.Range;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import javax.annotation.PostConstruct;
import javax.swing.text.*;
import java.awt.*;
import java.util.*;

/**
 * A data model representing a field part on a card.
 * See {@link FieldPart} for the associated controller object. This model is a mess. Just go with it:
 * <p>
 * First: HyperCard mixes rich text (as edited by the user in the view) with plaintext (as written or read via script).
 * To support this, the model persists the RTF rich text, but exposes a computed property ('text') that scripts can
 * read or write. When a script sets the field's text property, the model attempts to intelligently replace the
 * contents of the field with the new contents, keeping the existing style intact as best as possible. See
 * {@link #replaceText(String)} for details.
 * <p>
 * Second: Fields placed in the background layer can either share their contents across all cards in the background
 * or, each card may have its own text (while still sharing other properties like size, location and showLines). This
 * necessitates the {@link #sharedText} and {@link #unsharedText} properties. Foreground-layer fields always use the
 * {@link #sharedText} value.
 * <p>
 * Third: TextAlign is a separate, managed property of the field and not of the document model because Java's
 * RTFEditorKit doesn't support saving text alignment. Ugh! That's okay though, because HyperCard supports only a
 * single alignment per field, which we can model as a standard read/writable property in the
 * {@link com.defano.hypercard.parts.model.PropertiesModel}.
 * <p>
 * Fourth: Changes to the field's DOM can originate from the UI (i.e., a user typing into the field) or from HyperTalk.
 * Because changes can originate in the view ({@link HyperCardTextField}, this
 * requires a bidirectional observation binding: the model must observe the view for changes, and the view must observe
 * the model for changes..
 * <p>
 * Four-and-a-half: Not only do the view and the model need to observe one another, but some aspects of the model (like
 * rich-text data and selection ranges) are not readily modeled in the PropertiesModel, so they require thier own
 * observation API, {@link FieldModelObserver}.
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

    private StyledDocument sharedText = new DefaultStyledDocument();
    private final Map<Integer, StyledDocument> unsharedText = new HashMap<>();
    private final Set<Integer> sharedAutoSelection = new HashSet<>();
    private final Set<Integer> unsharedAutoSelection = new HashSet<>();

    private transient int currentCardId = 0;
    private transient FieldModelObserver observer;
    private transient Range selection;

    public FieldModel(Owner owner) {
        super(PartType.FIELD, owner);
    }

    public static FieldModel newFieldModel(int id, Rectangle geometry, Owner owner, int parentCardId) {
        FieldModel partModel = new FieldModel(owner);

        partModel.currentCardId = parentCardId;

        partModel.defineProperty(PROP_SCRIPT, new Value(), false);
        partModel.defineProperty(PROP_ID, new Value(id), true);
        partModel.defineProperty(PROP_NAME, new Value("Text Field " + id), false);
        partModel.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        partModel.defineProperty(PROP_TOP, new Value(geometry.y), false);
        partModel.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        partModel.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        partModel.defineProperty(PROP_DONTWRAP, new Value(false), false);
        partModel.defineProperty(PROP_VISIBLE, new Value(true), false);
        partModel.defineProperty(PROP_LOCKTEXT, new Value(false), false);
        partModel.defineProperty(PROP_SHOWLINES, new Value(true), false);
        partModel.defineProperty(PROP_STYLE, new Value(FieldStyle.TRANSPARENT.getName()), false);
        partModel.defineProperty(PROP_TEXTALIGN, new Value("left"), false);
        partModel.defineProperty(PROP_CONTENTS, new Value(""), false);
        partModel.defineProperty(PROP_SHAREDTEXT, new Value(false), false);
        partModel.defineProperty(PROP_WIDEMARGINS, new Value(false), false);
        partModel.defineProperty(PROP_AUTOTAB, new Value(false), false);
        partModel.defineProperty(PROP_AUTOSELECT, new Value(false), false);
        partModel.defineProperty(PROP_MULTIPLELINES, new Value(false), false);
        partModel.defineProperty(PROP_SCROLLING, new Value(true), false);
        partModel.defineProperty(PROP_SCROLL, new Value(0), false);

        partModel.initialize();

        return partModel;
    }

    /** {@inheritDoc} */
    @PostConstruct
    @Override
    public void initialize() {
        super.initialize();

        defineComputedGetterProperty(PROP_TEXT, (model, propertyName) -> new Value(getText()));
        defineComputedSetterProperty(PROP_TEXT, (model, propertyName, value) -> replaceText(value.stringValue()));

        defineComputedGetterProperty(PROP_TEXTFONT, (model, propertyName) -> new Value(getTextFontFamily(0, getText().length() + 1)));
        defineComputedSetterProperty(PROP_TEXTFONT, (model, propertyName, value) -> setTextFontFamily(0, getText().length() + 1, value));

        defineComputedGetterProperty(PROP_TEXTSIZE, (model, propertyName) -> new Value(getTextFontSize(0, getText().length() + 1)));
        defineComputedSetterProperty(PROP_TEXTSIZE, (model, propertyName, value) -> setTextFontSize(0, getText().length() + 1, value));

        defineComputedGetterProperty(PROP_TEXTSTYLE, (model, propertyName) -> new Value(getTextFontStyle(0, getText().length() + 1)));
        defineComputedSetterProperty(PROP_TEXTSTYLE, (model, propertyName, value) -> setTextFontStyle(0, getText().length() + 1, value));

        addPropertyChangedObserver(LogicalLinkObserver.setOnSet(PROP_AUTOSELECT, PROP_DONTWRAP));
        addPropertyChangedObserver(LogicalLinkObserver.setOnSet(PROP_AUTOSELECT, PROP_LOCKTEXT));
    }

    /**
     * Sets the observer of scripted changes to the field's document model. Only a single observer is supported (and
     * should always be the field view object, {@link HyperCardTextField}.
     *
     * In most every other case, a special observer interface is not required because observable attributes are modeled
     * by {@link com.defano.hypercard.parts.model.PropertiesModel}. Unfortunately, this technique requires properties
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
     * @return A StyledDocument representation of the contents of this field.
     */
    public StyledDocument getStyledDocument() {
        if (useSharedText()) {
            return sharedText == null ? new DefaultStyledDocument() : sharedText;
        } else {
            return unsharedText.getOrDefault(currentCardId, new DefaultStyledDocument());
        }
    }

    /**
     * Persists the given StyledDocument data into this model. Affects either the shared document data, or the unshared
     * data depending on whether the field is in the background and has the sharedText property.
     *
     * @param doc The styled document data to persist into the model.
     */
    public void setStyledDocument(StyledDocument doc) {
        if (useSharedText()) {
            FieldModel.this.sharedText = doc;
        } else {
            unsharedText.put(currentCardId, doc);
        }
    }

    /**
     * Determine if the model should use the sharedText document data.
     *
     * @return True if the model should use sharedText data; false otherwise.
     */
    private boolean useSharedText() {
        return getOwner() == Owner.CARD || getKnownProperty(PROP_SHAREDTEXT).booleanValue();
    }

    /**
     * Gets a plaintext representation of the text held in this model.
     *
     * @return A plaintext representation of the contents of this field.
     */
    @Override
    public String getText() {
        StyledDocument doc = getStyledDocument();
        try {
            return doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            return "";
        }
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
     * @param newText The text with which to replace the field's existing contents.
     */
    private void replaceText(String newText) {
        String existingText = getText();

        // Don't waste our own time
        if (newText.equals(existingText)) {
            return;
        }

        int changePosition = 0;
        StyledDocument document = getStyledDocument();

        AttributeSet style = document.getLength() == 0 ?
                FontContext.getInstance().getFocusedTextStyle().toAttributeSet() :
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

        setStyledDocument(document);              // Save our changes
        fireDocumentChangeObserver(document);     // ... and let the view know know about 'em
    }

    /**
     * Produces a set of differences between the existing and replacement strings.
     *
     * @param existing The existing text to analyze
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
     * For the purposes of background fields that do not have the sharedText property set, this value determines which
     * card's text is actively displayed in the field.
     * <p>
     * We cannot simply query the active card ({@link ExecutionContext#getCurrentCard()}) to derive this value because
     * part models are initialized <i>before</i> the card is changed.
     *
     * @param cardId The ID of the card on which this field is currently being displayed.
     */
    public void setCurrentCardId(int cardId) {
        this.currentCardId = cardId;
    }

    /** {@inheritDoc} */
    @Override
    public void setTextStyle(TextStyleSpecifier tss) {
        StyledDocument doc = getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength() + 1, tss.toAttributeSet(), true);

        setStyledDocument(doc);
        fireDocumentChangeObserver(doc);
    }

    /** {@inheritDoc} */
    @Override
    public TextStyleSpecifier getTextStyle() {
        return TextStyleSpecifier.fromAttributeSet(getStyledDocument().getCharacterElement(0).getAttributes());
    }

    /**
     * Sets the font family of the indicated range of characters in this field; has no effect if the font family
     * is not available on this system.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontFamily    The new font family to apply.
     */
    public void setTextFontFamily(int startPosition, int length, Value fontFamily) {
        StyledDocument doc = getStyledDocument();

        // Special case; zero-length document does not persist prev style, replace with focused style
        if (doc.getLength() == 0) {
            TextStyleSpecifier tss = FontContext.getInstance().getFocusedTextStyle();
            tss.setFontFamily(fontFamily.stringValue());
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), true);
        }

        // Apply font family to document
        else {
            TextStyleSpecifier tss = TextStyleSpecifier.fromFontFamily(fontFamily.stringValue());
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), false);
        }

        setStyledDocument(doc);
        fireDocumentChangeObserver(doc);
    }

    /**
     * Sets the font size (in points) of the indicated range of characters in this field.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontSize      The new font size to apply.
     */
    public void setTextFontSize(int startPosition, int length, Value fontSize) {
        StyledDocument doc = getStyledDocument();

        // Special case; zero-length document does not persist prev style, replace with focused style
        if (doc.getLength() == 0) {
            TextStyleSpecifier tss = FontContext.getInstance().getFocusedTextStyle();
            tss.setFontSize(fontSize.integerValue());
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), true);
        }

        // Apply font size to document
        else {
            TextStyleSpecifier tss = TextStyleSpecifier.fromFontSize(fontSize.integerValue());
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), false);
        }

        setStyledDocument(doc);
        fireDocumentChangeObserver(doc);
    }

    /**
     * Sets the font style of the indicated range of characters in this field; style should be 'italic', 'bold',
     * 'bold,italic' or 'plain'.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontStyle     The new font style to apply.
     */
    public void setTextFontStyle(int startPosition, int length, Value fontStyle) {
        StyledDocument doc = getStyledDocument();

        // Special case; zero-length document does not persist prev style, replace with focused style
        if (doc.getLength() == 0) {
            TextStyleSpecifier tss = FontContext.getInstance().getFocusedTextStyle();
            tss.setFontStyle(fontStyle);
            doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), true);
        }

        // Apply style changes to each character
        else {
            for (int index = startPosition; index < startPosition + length; index++) {
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(getStyledDocument().getCharacterElement(index).getAttributes());
                tss.setFontStyle(fontStyle);
                doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
            }
        }

        setStyledDocument(doc);
        fireDocumentChangeObserver(doc);
    }

    /**
     * Gets the font family of the indicated range of characters in the field, or 'mixed' if multiple fonts are present
     * in the range.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @return              The name of the font family present in the range of characters or 'mixed' if there are
     * multiple fonts
     */
    public Value getTextFontFamily(int startPosition, int length) {
        for (int index = startPosition; index < startPosition + length - 1; index++) {
            if (!getTextFontFamily(index).equals(getTextFontFamily(index + 1))) {
                return new Value("mixed");
            }
        }
        return new Value(getTextFontFamily(startPosition));
    }

    private Value getTextFontFamily(int position) {
        return new Value(getStyledDocument().getCharacterElement(position).getAttributes().getAttribute(StyleConstants.FontFamily));
    }

    /**
     * Gets the font size of the indicated range of characters in the field, or 'mixed' if multiple sizes are present
     * in the range.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @return              The size of the font present in the range of characters or 'mixed' if there are multiple
     * sizes.
     */
    public Value getTextFontSize(int startPosition, int length) {
        for (int index = startPosition; index < startPosition + length - 1; index++) {
            if (!getTextFontSize(index).equals(getTextFontSize(index + 1))) {
                return new Value("mixed");
            }
        }
        return new Value(getTextFontSize(startPosition));
    }

    private Value getTextFontSize(int position) {
        return new Value(getStyledDocument().getCharacterElement(position).getAttributes().getAttribute(StyleConstants.FontSize));
    }

    /**
     * Gets the font style of the indicated range of characters in the field, or 'mixed' if multiple styles are present
     * in the range.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @return              The font style present in the range of characters or 'mixed' if there are multiple styles
     */
    public Value getTextFontStyle(int startPosition, int length) {
        for (int index = startPosition; index < startPosition + length - 1; index++) {
            if (!getTextFontStyle(index).equals(getTextFontStyle(index + 1))) {
                return new Value("mixed");
            }
        }
        return new Value(getTextFontStyle(startPosition));
    }

    private Value getTextFontStyle(int position) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(getStyledDocument().getCharacterElement(position).getAttributes());
        return tss.getHyperTalkStyle();
    }

    /**
     * Auto-selects the given line number. Auto-selecting the line highlights the entire width of the line and places
     * the contents of the line into the selection, Has no effect if the field has no such line.
     *
     * @param lineNumber The line number to auto-select
     */
    public void autoSelectLine(int lineNumber, boolean appendSelection) {
        if (lineNumber >= 1 && lineNumber <= new Value(getText()).getLines().size()) {

            if (!appendSelection) {
                getAutoSelectedLines().clear();
            }

            getAutoSelectedLines().add(lineNumber);
            fireAutoSelectChangeObserver(getAutoSelectedLines());
        }
    }

    /**
     * Auto-selects the given range of lines.
     *
     * @param startLine The first line in the auto-selection, counting from 1, inclusive.
     * @param endLine The last line in the auto-selection, counting from 1, inclusive.
     */
    private void autoSelectLines(int startLine, int endLine) {
        Set<Integer> autoSelection = getAutoSelectedLines();

        autoSelection.clear();
        for (int line = startLine; line <= endLine; line++) {
            autoSelection.add(line);
        }

        fireAutoSelectChangeObserver(autoSelection);
    }

    /**
     * Returns the set of lines currently that are currently auto-selected, or an empty set if auto-selection is
     * disabled.
     *
     * @return The auto-selected lines.
     */
    public Set<Integer> getAutoSelectedLines() {
        if (isAutoSelection()) {
            return useSharedText() ? sharedAutoSelection : unsharedAutoSelection;
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Returns the range of characters in the field document represented by the auto-selection, or an empty range
     * if auto-selection is disabled.
     *
     * @return The range of characters in the auto selection.
     */
    private Range getAutoSelectionRange() {
        if (isAutoSelection()) {
            int[] lines = getAutoSelectedLines().stream().mapToInt(Number::intValue).toArray();
            return FieldUtilities.getLinesRange(getText(), lines);
        } else {
            return new Range();
        }
    }

    /**
     * Determines if the auto-selection property is enabled on this field.
     * @return True if auto-selection is enabled; false otherwise
     */
    public boolean isAutoSelection() {
        return getKnownProperty(FieldModel.PROP_AUTOSELECT).booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelection(Range selection) {
        if (isAutoSelection()) {
            autoSelectLines(FieldUtilities.getLineOfChar(selection.start, getText()), FieldUtilities.getLineOfChar(selection.end, getText()));
        } else {
            this.selection = selection;
            fireSelectionChange(selection);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Range getSelection() {
        if (isAutoSelection()) {
            return getAutoSelectionRange();
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
    public String getHyperTalkAddress() {
        return getOwner().hyperTalkName.toLowerCase() + " field id " + getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartSpecifier getPartSpecifier() {
        return new PartIdSpecifier(getOwner(), PartType.FIELD, getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldModel getSelectableTextModel() {
        return this;
    }

    private void fireAutoSelectChangeObserver(Set<Integer> selectedLines) {
        if (observer != null) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> observer.onAutoSelectionChanged(selectedLines));
        }
    }

    private void fireDocumentChangeObserver(StyledDocument document) {
        if (observer != null) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> observer.onStyledDocumentChanged(document));
        }
    }

    private void fireSelectionChange(Range selection) {
        if (observer != null) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> observer.onSelectionChange(selection));
        }
    }

}

