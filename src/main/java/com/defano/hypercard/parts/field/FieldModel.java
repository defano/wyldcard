package com.defano.hypercard.parts.field;

import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import javax.annotation.PostConstruct;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A data model representing a field part on a card. See {@link FieldPart} for the associated controller object. This
 * model is a mess. Just go with it:
 * <p>
 * First: HyperCard mixes rich text (as edited by the user in the view) with plaintext (as written or read via script).
 * To support this, the model persists the RTF rich text, but exposes a computed property ('text') that scripts can
 * read or write. When a script sets the field's text property, the model attempts to "intelligently" replace the
 * contents of the field with the new contents, keeping the existing style intact as best as possible. See
 * {@link #replaceText(String)} for details.
 * <p>
 * Second: Fields placed in the background layer can either share their contents across all cards in the background
 * or, each card may have its own text (while sharing other field properties like size, location and showLines).
 * <p>
 * Third: TextAlign is a separate, managed property of the field and not of the document model because Java's
 * RTFEditorKit doesn't support saving text alignment. Doh! That's okay, though, because HyperCard supports only a
 * single alignment per field, which we can model as a standard read/writable property.
 * <p>
 * Fourth: Changes to the field's DOM can originate from the UI (i.e., a user typing into the field) or from HyperTalk.
 * Because changes can originate in the view ({@link com.defano.hypercard.parts.field.styles.AbstractTextField}, this
 * requires us, the model, to observe the view for changes and for the view to observe us, the model, for changes. Not
 * ideal...
 */
public class FieldModel extends CardLayerPartModel {

    public static final String PROP_TEXT = "text";
    public static final String PROP_DONTWRAP = "dontwrap";
    public static final String PROP_LOCKTEXT = "locktext";
    public static final String PROP_SHOWLINES = "showlines";
    public static final String PROP_STYLE = "style";
    public static final String PROP_SHAREDTEXT = "sharedtext";
    public static final String PROP_WIDEMARGINS = "widemargins";
    public static final String PROP_AUTOTAB = "autotab";

    private byte[] sharedRtf;
    private Map<Integer, byte[]> unsharedRtf = new HashMap<>();

    private transient int currentCardId = 0;
    private transient FieldDocumentObserver observer;

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

        partModel.initialize();

        return partModel;
    }

    @PostConstruct
    @Override
    public void initialize() {
        super.initialize();

        defineComputedGetterProperty(PROP_TEXT, (model, propertyName) -> new Value(getPlainText()));
        defineComputedSetterProperty(PROP_TEXT, (model, propertyName, value) -> replaceText(value.stringValue()));

        defineComputedGetterProperty(PROP_TEXTFONT, (model, propertyName) -> new Value(getTextFontFamily(0, getPlainText().length())));
        defineComputedSetterProperty(PROP_TEXTFONT, (model, propertyName, value) -> setTextFontFamily(0, getPlainText().length(), value));

        defineComputedGetterProperty(PROP_TEXTSIZE, (model, propertyName) -> new Value(getTextFontSize(0, getPlainText().length())));
        defineComputedSetterProperty(PROP_TEXTSIZE, (model, propertyName, value) -> setTextFontSize(0, getPlainText().length(), value));

        defineComputedGetterProperty(PROP_TEXTSTYLE, (model, propertyName) -> new Value(getTextFontStyle(0, getPlainText().length())));
        defineComputedSetterProperty(PROP_TEXTSTYLE, (model, propertyName, value) -> setTextFontStyle(0, getPlainText().length(), value));
    }

    /**
     * Sets the observer of scripted changes to the field's document model. Only a single observer is supported (and
     * should always be the field view object, {@link com.defano.hypercard.parts.field.styles.AbstractTextField}.
     *
     * In most every other case, a special observer interface is not required because observable attributes are modeled
     * by {@link com.defano.hypercard.parts.model.PropertiesModel}. Unfortunately, this technique requires properties
     * to be modeled as a HyperTalk {@link Value}. Coercing a byte array into and out of a Value would be ugly.
     *
     * @param observer The observer of DOM changes.
     */
    public void setDocumentObserver(FieldDocumentObserver observer) {
        this.observer = observer;
    }

    /**
     * Gets a Swing {@link StyledDocument} representing the rich text displayed in this field.
     *
     * @return A StyledDocument representation of the contents of this field.
     */
    public StyledDocument getStyledDocument() {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(getRtf());
            StyledDocument doc = new DefaultStyledDocument();

            new RTFEditorKit().read(bais, doc, 0);
            bais.close();

            // RTFEditorKit appears to (erroneously) append a newline when we deserialize; get rid of that.
            doc.remove(doc.getLength() - 1, 1);

            return doc;

        } catch (Exception e) {
            return new DefaultStyledDocument();
        }
    }

    /**
     * Persists the given StyledDocument data into this model. Affects either the shared document data, or the unshared
     * data depending on whether the field is in the background and has the sharedText property.
     *
     * @param doc The styled document data to persist into the model.
     */
    public void setStyledDocument(StyledDocument doc) {
        byte[] rtf = convertDocumentToRtf(doc);

        if (useSharedText()) {
            this.sharedRtf = rtf;
        } else {
            unsharedRtf.put(currentCardId, rtf);
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
    private String getPlainText() {
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

        String existingText = getPlainText();

        // Don't waste our own time
        if (newText.equals(existingText)) {
            return;
        }

        int changePosition = 0;
        StyledDocument document = getStyledDocument();
        AttributeSet style = document.getCharacterElement(0).getAttributes();

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

        setStyledDocument(document);        // Save our changes
        fireDocumentObserver(document);     // ... and let the view know know about 'em
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

    /**
     * Sets the font family of the indicated range of characters in this field; has no effect if the font family
     * is not available on this system.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontFamily    The new font family to apply.
     */
    public void setTextFontFamily(int startPosition, int length, Value fontFamily) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontFamily(fontFamily.stringValue());
        StyledDocument doc = getStyledDocument();
        doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), false);
        fireDocumentObserver(doc);
    }

    /**
     * Sets the font size (in points) of the indicated range of characters in this field.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontSize      The new font size to apply.
     */
    public void setTextFontSize(int startPosition, int length, Value fontSize) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontSize(fontSize.integerValue());
        StyledDocument doc = getStyledDocument();
        doc.setCharacterAttributes(startPosition, length, tss.toAttributeSet(), false);
        fireDocumentObserver(doc);
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
        for (int index = startPosition; index <= startPosition + length; index++) {
            TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(getStyledDocument().getCharacterElement(index).getAttributes());
            tss.setFontStyle(fontStyle);
            doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
        }
        fireDocumentObserver(doc);
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

    public Value getTextFontFamily(int position) {
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

    public Value getTextFontSize(int position) {
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

    public Value getTextFontStyle(int position) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(getStyledDocument().getCharacterElement(position).getAttributes());
        return tss.getHyperTalkStyle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueProperty() {
        return PROP_TEXT;
    }

    private byte[] convertDocumentToRtf(StyledDocument doc) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            new RTFEditorKit().write(baos, doc, 0, doc.getLength());
            baos.close();

            return baos.toByteArray();

        } catch (IOException | BadLocationException e) {
            throw new RuntimeException("An error occurred while saving field contents.", e);
        }
    }

    /**
     * Gets the RTF data persisted in this model. Retrieves either the shared document data, or the unshared data
     * depending on whether the field is in the background and has the sharedText property.
     *
     * @return The RTF data persisted into the model, or an empty document if nothing has been persisted.
     */
    private byte[] getRtf() {
        if (useSharedText()) {
            return sharedRtf;
        } else {
            return unsharedRtf.getOrDefault(currentCardId, new byte[0]);
        }
    }

    private void fireDocumentObserver(StyledDocument document) {
        if (observer != null) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> observer.onStyledDocumentChanged(document));
        }
    }

}

