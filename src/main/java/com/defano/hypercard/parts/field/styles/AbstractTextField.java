package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.paint.FontContext;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.field.FieldComponent;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.utils.Range;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * An abstract HyperCard text field; that is, one without a specific style bound to it. Encapsulates the stylable,
 * editable text component and the scrollable surface in which it is embedded.
 */
public abstract class AbstractTextField extends JScrollPane implements FieldComponent, DocumentListener, CaretListener {

    private final HyperCardTextPane textPane;
    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private final FontSizeObserver fontSizeObserver = new FontSizeObserver();
    private final FontStyleObserver fontStyleObserver = new FontStyleObserver();
    private final FontFamilyObserver fontFamilyObserver = new FontFamilyObserver();

    private final ToolEditablePart toolEditablePart;
    private AttributeSet currentStyle = new SimpleAttributeSet();

    public AbstractTextField(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        // Create the editor component
        textPane = new HyperCardTextPane(new DefaultStyledDocument());
        textPane.setEditorKit(new RTFEditorKit());
        this.setViewportView(textPane);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setRtf(getRtf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setRtf(getRtf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setRtf(getRtf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case FieldModel.PROP_TEXT:
                replaceText(newValue.stringValue());
                break;

            case FieldModel.PROP_DONTWRAP:
                textPane.setWrapText(!newValue.booleanValue());
                break;

            case FieldModel.PROP_LOCKTEXT:
                textPane.setEditable(!newValue.booleanValue());
                break;

            case FieldModel.PROP_SHOWLINES:
                textPane.setShowLines(newValue.booleanValue());
                break;

            case FieldModel.PROP_TEXTALIGN:
                setActiveTextAlign(newValue);
                break;

            case FieldModel.PROP_TEXTSIZE:
                setTextFontSize(0, getText().length(), newValue);
                break;

            case FieldModel.PROP_TEXTSTYLE:
                setTextFontStyle(0, getText().length(), newValue);
                break;

            case FieldModel.PROP_TEXTFONT:
                setTextFontFamily(0, getText().length(), newValue);
                break;

            case FieldModel.PROP_ENABLED:
                toolEditablePart.setEnabledOnCard(newValue.booleanValue());
                break;
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
     * TODO: Change much infrastructure to allow model to report inserts and deletes instead of using this "cheat".
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
        StyledDocument document = textPane.getStyledDocument();
        AttributeSet style = currentStyle;

        // Do not perform incremental model updates while we update
        document.removeDocumentListener(this);

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
        } finally {
            document.addDocumentListener(this);
        }

        ((FieldModel) toolEditablePart.getPartModel()).setRtf(getRtf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFontFamily(int startPosition, int length, Value fontFamily) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontFamily(fontFamily.stringValue());

        if (length > 0) {
            textPane.getStyledDocument().setCharacterAttributes(startPosition, length, tss.toAttributeSet(), false);
        } else {
            textPane.setCharacterAttributes(tss.toAttributeSet(), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFontSize(int startPosition, int length, Value fontSize) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontSize(fontSize.integerValue());

        if (length > 0) {
            textPane.getStyledDocument().setCharacterAttributes(startPosition, length, tss.toAttributeSet(), false);
        } else {
            textPane.setCharacterAttributes(tss.toAttributeSet(), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextFontStyle(int startPosition, int length, Value fontStyle) {
        if (length > 0) {
            for (int index = startPosition; index < startPosition + length; index++) {
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(textPane.getStyledDocument().getCharacterElement(index).getAttributes());
                tss.setFontStyle(fontStyle);
                textPane.getStyledDocument().setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
            }
        } else {
            TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(textPane.getCharacterAttributes());
            tss.setFontStyle(fontStyle);
            textPane.setCharacterAttributes(tss.toAttributeSet(), true);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value getTextFontFamily(int startPosition, int length) {
        for (int index = startPosition; index < startPosition + length - 1; index++) {
            if (!getTextFontFamily(index).equals(getTextFontFamily(index + 1))) {
                return new Value("mixed");
            }
        }
        return new Value(getTextFontFamily(startPosition));
    }

    private Value getTextFontFamily(int position) {
        return new Value(textPane.getStyledDocument().getCharacterElement(position).getAttributes().getAttribute(StyleConstants.FontFamily));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value getTextFontSize(int startPosition, int length) {
        for (int index = startPosition; index < startPosition + length - 1; index++) {
            if (!getTextFontSize(index).equals(getTextFontSize(index + 1))) {
                return new Value("mixed");
            }
        }
        return new Value(getTextFontSize(startPosition));
    }

    private Value getTextFontSize(int position) {
        return new Value(textPane.getStyledDocument().getCharacterElement(position).getAttributes().getAttribute(StyleConstants.FontSize));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value getTextFontStyle(int startPosition, int length) {
        for (int index = startPosition; index < startPosition + length - 1; index++) {
            if (!getTextFontStyle(index).equals(getTextFontStyle(index + 1))) {
                return new Value("mixed");
            }
        }
        return new Value(getTextFontStyle(startPosition));
    }

    private Value getTextFontStyle(int position) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(textPane.getStyledDocument().getCharacterElement(position).getAttributes());
        return tss.getHyperTalkStyle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        try {
            String text = textPane.getStyledDocument().getText(0, textPane.getStyledDocument().getLength());
            return (text == null) ? "" : text;
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JTextPane getTextPane() {
        return textPane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEditable(boolean editable) {
        super.setEnabled(editable);
        textPane.setEditable(editable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partOpened() {

        // Listen for changes to the field's contents
        textPane.getStyledDocument().addDocumentListener(this);

        FontContext.getInstance().getSelectedFontFamilyProvider().addObserver(fontFamilyObserver);
        FontContext.getInstance().getSelectedFontStyleProvider().addObserver(fontStyleObserver);
        FontContext.getInstance().getSelectedFontSizeProvider().addObserver(fontSizeObserver);

        // Get notified when field tool is selected
        ToolsContext.getInstance().getToolModeProvider().addObserver(toolModeObserver);

        // Add mouse and keyboard listeners
        textPane.addMouseListener(toolEditablePart);
        textPane.addCaretListener(this);

        ToolsContext.getInstance().getToolModeProvider().notifyObservers(toolModeObserver);
        setRtf(((FieldModel) toolEditablePart.getPartModel()).getRtf());

        // Finally, update view with model data
        toolEditablePart.getPartModel().notifyPropertyChangedObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partClosed() {
        textPane.getStyledDocument().removeDocumentListener(this);

        textPane.removeMouseListener(toolEditablePart);
        textPane.removeCaretListener(this);

        toolEditablePart.getPartModel().removePropertyChangedObserver(this);

        FontContext.getInstance().getSelectedFontFamilyProvider().deleteObserver(fontFamilyObserver);
        FontContext.getInstance().getSelectedFontStyleProvider().deleteObserver(fontStyleObserver);
        FontContext.getInstance().getSelectedFontSizeProvider().deleteObserver(fontSizeObserver);

        ToolsContext.getInstance().getToolModeProvider().deleteObserver(toolModeObserver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void caretUpdate(CaretEvent e) {
        // Update selectedText
        toolEditablePart.getPartModel().defineProperty(FieldModel.PROP_SELECTEDTEXT, new Value(textPane.getSelectedText()), true);

        // Update global font style selection
        Range selection = getSelectedTextRange();
        AttributeSet caretAttributes = selection.isEmpty() ?
                textPane.getCharacterAttributes() :
                textPane.getStyledDocument().getCharacterElement(selection.start + ((selection.end - selection.start) / 2)).getAttributes();

        FontContext.getInstance().setHilitedTextStyle(TextStyleSpecifier.fromAttributeSet(caretAttributes));
    }

    private LinkedList<DiffMatchPatch.Diff> getTextDifferences(String existing, String replacement) {
        DiffMatchPatch dmp = new DiffMatchPatch();

        LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(existing, replacement);
        dmp.diffCleanupSemantic(diffs);

        return diffs;
    }

    private byte[] getRtf() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document doc = textPane.getStyledDocument();
            textPane.getEditorKit().write(baos, doc, 0, doc.getLength());
            baos.close();

            return baos.toByteArray();

        } catch (IOException | BadLocationException e) {
            throw new RuntimeException("An error occurred while saving field contents.", e);
        }
    }

    private void setRtf(byte[] rtfData) {

        if (rtfData != null && rtfData.length != 0) {
            ByteArrayInputStream bais = new ByteArrayInputStream(rtfData);
            try {
                StyledDocument doc = new DefaultStyledDocument();

                textPane.getEditorKit().read(bais, doc, 0);
                bais.close();

                // RTFEditorKit appears to (erroneously) append a newline when we deserialize; get rid of that.
                doc.remove(doc.getLength() - 1, 1);

                textPane.setStyledDocument(doc);
                doc.addDocumentListener(this);

            } catch (IOException | BadLocationException e) {
                throw new RuntimeException("An error occurred while restoring field contents.", e);
            }
        }
    }

    private Range getSelectedTextRange() {
        return new Range(textPane.getCaret().getDot(), textPane.getCaret().getMark());
    }

    private void setActiveTextAlign(Value v) {
        SimpleAttributeSet alignment = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignment, FontUtils.getAlignmentStyleForValue(v));
        textPane.getStyledDocument().setParagraphAttributes(0, textPane.getStyledDocument().getLength(), alignment, false);
    }

    private class FontStyleObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            Range selection = getSelectedTextRange();
            setTextFontStyle(selection.start, selection.length(), (Value) arg);
        }
    }

    private class FontSizeObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            Range selection = getSelectedTextRange();
            setTextFontSize(selection.start, selection.length(), (Value) arg);
        }
    }

    private class FontFamilyObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            Range selection = getSelectedTextRange();
            setTextFontFamily(selection.start, selection.length(), (Value) arg);
        }
    }

    private class ToolModeObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            setHorizontalScrollBarPolicy(ToolMode.FIELD == arg ? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER : ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            setVerticalScrollBarPolicy(ToolMode.FIELD == arg ? ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER : ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            setEditable(ToolMode.FIELD != arg && !toolEditablePart.getPartModel().getKnownProperty(FieldModel.PROP_LOCKTEXT).booleanValue());
        }
    }
}
