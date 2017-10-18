package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.awt.KeyListenable;
import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.paint.FontContext;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.field.FieldComponent;
import com.defano.hypercard.parts.field.FieldDocumentObserver;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.utils.Range;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * An abstract HyperCard text field; that is, one without a specific style bound to it. Encapsulates the stylable,
 * editable text component and the scrollable surface in which it is embedded.
 */
public abstract class AbstractTextField extends JScrollPane implements FieldComponent, DocumentListener, CaretListener, FieldDocumentObserver {

    public final static int WIDE_MARGIN_PX = 15;
    public final static int NARROW_MARGIN_PX = 0;

    private final HyperCardTextPane textPane;
    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private final FontSizeObserver fontSizeObserver = new FontSizeObserver();
    private final FontStyleObserver fontStyleObserver = new FontStyleObserver();
    private final FontFamilyObserver fontFamilyObserver = new FontFamilyObserver();
    private final AutoTabKeyObserver autoTabKeyObserver = new AutoTabKeyObserver();

    private final ToolEditablePart toolEditablePart;

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
        updateModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        updateModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        updateModel();
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

            case FieldModel.PROP_ENABLED:
                toolEditablePart.setEnabledOnCard(newValue.booleanValue());
                break;

            case FieldModel.PROP_WIDEMARGINS:
                setWideMargins(newValue.booleanValue());
                break;
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

        // Get notified when field tool is selected
        ToolsContext.getInstance().getToolModeProvider().addObserver(toolModeObserver);
        ToolsContext.getInstance().getToolModeProvider().notifyObservers(toolModeObserver);

        FontContext.getInstance().getSelectedFontFamilyProvider().addObserver(fontFamilyObserver);
        FontContext.getInstance().getSelectedFontStyleProvider().addObserver(fontStyleObserver);
        FontContext.getInstance().getSelectedFontSizeProvider().addObserver(fontSizeObserver);

        // React to scripted changes to the field model
        ((FieldModel) toolEditablePart.getPartModel()).setDocumentObserver(this);

        // Add mouse and keyboard listeners
        textPane.addMouseListener(toolEditablePart);
        textPane.addCaretListener(this);
        textPane.addKeyListener(autoTabKeyObserver);

        // Finally, update view with model data
        displayStyledDocument(((FieldModel) toolEditablePart.getPartModel()).getStyledDocument());
        toolEditablePart.getPartModel().notifyPropertyChangedObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partClosed() {
        updateModel();
        textPane.getStyledDocument().removeDocumentListener(this);

        textPane.removeMouseListener(toolEditablePart);
        textPane.removeCaretListener(this);
        textPane.addKeyListener(autoTabKeyObserver);

        toolEditablePart.getPartModel().removePropertyChangedObserver(this);
        ((FieldModel) toolEditablePart.getPartModel()).setDocumentObserver(null);

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStyledDocumentChanged(StyledDocument document) {
        displayStyledDocument(document);
    }

    private void displayStyledDocument(StyledDocument doc) {
        if (textPane.getStyledDocument() != doc) {
            textPane.setStyledDocument(doc);
            doc.addDocumentListener(this);
        }
    }

    private Range getSelectedTextRange() {
        return new Range(textPane.getCaret().getDot(), textPane.getCaret().getMark());
    }

    private void setActiveTextAlign(Value v) {
        SimpleAttributeSet alignment = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignment, FontUtils.getAlignmentStyleForValue(v));
        textPane.getStyledDocument().setParagraphAttributes(0, textPane.getStyledDocument().getLength(), alignment, false);

        updateModel();
    }

    private void setTextFontFamily(Value fontFamily) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontFamily(fontFamily.stringValue());
        textPane.setCharacterAttributes(tss.toAttributeSet(), false);

        updateModel();
    }

    private void setTextFontSize(Value fontSize) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontSize(fontSize.integerValue());
        textPane.setCharacterAttributes(tss.toAttributeSet(), false);

        updateModel();
    }

    private void setTextFontStyle(Value fontStyle) {
        Range selection = getSelectedTextRange();
        StyledDocument doc = textPane.getStyledDocument();

        for (int index = selection.start; index < selection.start + selection.length(); index++) {
            TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(doc.getCharacterElement(index).getAttributes());
            tss.setFontStyle(fontStyle);
            doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
        }

        updateModel();
    }

    protected void setWideMargins(boolean isWideMargins) {
        if (isWideMargins) {
            textPane.setMargin(new Insets(WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX));
        } else {
            textPane.setMargin(new Insets(NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX));
        }

        textPane.invalidate(); textPane.repaint();
    }

    private void updateModel() {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setStyledDocument(textPane.getStyledDocument());
    }

    private class FontStyleObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> setTextFontStyle((Value) arg));
        }
    }

    private class FontSizeObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> setTextFontSize((Value) arg));
        }
    }

    private class FontFamilyObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> setTextFontFamily((Value) arg));
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

    private class AutoTabKeyObserver implements KeyListenable {
        /**
         * {@inheritDoc}
         */
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_TAB &&
                    toolEditablePart.getPartModel().getKnownProperty(FieldModel.PROP_AUTOTAB).booleanValue())
            {
                e.consume();
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            }
        }

    }
}
