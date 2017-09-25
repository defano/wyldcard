/*
 * AbstractTextPaneField
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.field.FieldComponent;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypertalk.ast.common.Value;
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
public abstract class AbstractTextField extends JScrollPane implements FieldComponent, DocumentListener, Observer, CaretListener {

    private final HyperCardTextPane textPane;
    private final ToolModeObserver toolModeObserver = new ToolModeObserver();

    private final ToolEditablePart toolEditablePart;
    private MutableAttributeSet currentStyle = new SimpleAttributeSet();

    public AbstractTextField(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        // Create the editor component
        textPane = new HyperCardTextPane(new DefaultStyledDocument());
        textPane.setEditorKit(new RTFEditorKit());
        this.setViewportView(textPane);

        // Listen for changes to the field's contents
        textPane.getStyledDocument().addDocumentListener(this);
        ToolsContext.getInstance().getSelectedFontProvider().addObserver(this);
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
        SwingUtilities.invokeLater(() -> {

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
                case FieldModel.PROP_TEXTSTYLE:
                case FieldModel.PROP_TEXTFONT:
                    setActiveFont(((CardLayerPartModel) toolEditablePart.getPartModel()).getFont());
                    break;

                case FieldModel.PROP_ENABLED:
                    setEditable(newValue.booleanValue());
                    break;
            }
        });
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
        textPane.setEnabled(editable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partOpened() {
        // Get notified when field tool is selected
        ToolsContext.getInstance().getToolModeProvider().addObserver(toolModeObserver);

        // Add mouse and keyboard listeners
        textPane.addMouseListener(toolEditablePart);
        textPane.addCaretListener(this);

        toolEditablePart.getPartModel().notifyPropertyChangedObserver(this);
        ToolsContext.getInstance().getToolModeProvider().notifyObservers(toolModeObserver);
        setRtf(((FieldModel) toolEditablePart.getPartModel()).getRtf());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partClosed() {
        textPane.removeMouseListener(toolEditablePart);
        textPane.removeCaretListener(this);

        toolEditablePart.getPartModel().removePropertyChangedObserver(this);

        ToolsContext.getInstance().getSelectedFontProvider().deleteObserver(this);
        ToolsContext.getInstance().getToolModeProvider().deleteObserver(toolModeObserver);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Font) {
            setActiveFont((Font) arg);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void caretUpdate(CaretEvent e) {
        // Update selectedText property
        toolEditablePart.getPartModel().defineProperty(FieldModel.PROP_SELECTEDTEXT, textPane.getSelectedText() == null ? new Value() : new Value(textPane.getSelectedText()), true);
        ExecutionContext.getContext().setSelectedText(textPane.getSelectedText() == null ? new Value() : new Value(textPane.getSelectedText()));

        // Update global font style selection
        AttributeSet caretAttributes = textPane.getStyledDocument().getCharacterElement(e.getMark()).getAttributes();
        ToolsContext.getInstance().getHilitedFontProvider().set(textPane.getStyledDocument().getFont(caretAttributes));
    }

    private LinkedList<DiffMatchPatch.Diff> getTextDifferences(String existing, String replacement) {
        DiffMatchPatch dmp = new DiffMatchPatch();

        LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(existing, replacement);
        dmp.diffCleanupSemantic(diffs);

        return diffs;
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

    private void setActiveTextAlign(Value v) {
        SimpleAttributeSet alignment = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignment, FontUtils.getAlignmentStyleForValue(v));
        textPane.getStyledDocument().setParagraphAttributes(0, textPane.getStyledDocument().getLength(), alignment, false);
    }

    private void setActiveFont(Font font) {
        if (getText().length() == 0) {
            textPane.setFont(font);
        } else {
            currentStyle = new SimpleAttributeSet();
            currentStyle.addAttribute(StyleConstants.FontFamily, font.getFamily());
            currentStyle.addAttribute(StyleConstants.Size, font.getSize());
            currentStyle.addAttribute(StyleConstants.Bold, font.isBold());
            currentStyle.addAttribute(StyleConstants.Italic, font.isItalic());

            textPane.setCharacterAttributes(currentStyle, true);
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
