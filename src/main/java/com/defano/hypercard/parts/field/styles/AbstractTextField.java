package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.awt.KeyListenable;
import com.defano.hypercard.awt.MouseListenable;
import com.defano.hypercard.awt.MouseMotionListenable;
import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.paint.FontContext;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.field.FieldComponent;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.field.FieldModelObserver;
import com.defano.hypercard.parts.model.PropertiesModel;
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
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * An abstract HyperCard text field; that is, one without a specific style bound to it. Encapsulates the stylable,
 * editable text component and the scrollable surface in which it is embedded.
 */
public abstract class AbstractTextField extends JScrollPane implements FieldComponent, DocumentListener, CaretListener, FieldModelObserver {

    public final static int WIDE_MARGIN_PX = 15;
    public final static int NARROW_MARGIN_PX = 1;

    private final HyperCardTextPane textPane;
    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private final FontSizeObserver fontSizeObserver = new FontSizeObserver();
    private final FontStyleObserver fontStyleObserver = new FontStyleObserver();
    private final FontFamilyObserver fontFamilyObserver = new FontFamilyObserver();
    private final AutoTabKeyObserver autoTabKeyObserver = new AutoTabKeyObserver();
    private final AutoSelectObserver autoSelectObserver = new AutoSelectObserver();

    private final ToolEditablePart toolEditablePart;

    public AbstractTextField(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        // Create the editor component
        textPane = new HyperCardTextPane(new DefaultStyledDocument());
        textPane.setEditorKit(new RTFEditorKit());
        textPane.setTransferHandler(null);      // Disallow drag-and-drop; causes issues with auto-select

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
    public void onPropertyChanged(PropertiesModel model, String property, Value oldValue, Value newValue) {
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

            case FieldModel.PROP_SCROLLING:
                textPane.setScrollable(newValue.booleanValue());
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
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();

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
        textPane.addMouseListener(autoSelectObserver);
        textPane.addMouseMotionListener(autoSelectObserver);

        // Update view with model data
        displayStyledDocument(model.getStyledDocument());
        toolEditablePart.getPartModel().notifyPropertyChangedObserver(this);

        // Initialize font to system font selection if document is empty
        if (textPane.getText().length() == 0) {
            textPane.setCharacterAttributes(FontContext.getInstance().getFocusedTextStyle().toAttributeSet(), true);
        }

        // And auto-select any
        SwingUtilities.invokeLater(() -> textPane.autoSelectLines(model.getAutoSelectedLines()));
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
        textPane.removeMouseListener(autoSelectObserver);

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
        FieldModel fieldModel = (FieldModel) toolEditablePart.getPartModel();
        if (fieldModel.isAutoSelection()) {
            fieldModel.updateSelectionContext(getSelectedTextRange(), fieldModel, false);
        } else {
            fieldModel.updateSelectionContext(Range.ofMarkAndDot(e.getDot(), e.getMark()), fieldModel, true);
        }

        // Update font style selection in menus
        if (textPane.getText().length() > 0 && textPane.isEditable()) {
            updateFocusedFontSelection();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStyledDocumentChanged(StyledDocument document) {
        displayStyledDocument(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAutoSelectedLinesChanged(Set<Integer> selectedLines) {
        textPane.autoSelectLines(selectedLines);
        updateFocusedFontSelection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectionChange(Range selection) {
        textPane.requestFocus();
        textPane.setSelectionStart(selection.start);
        textPane.setSelectionEnd(selection.end);
        updateFocusedFontSelection();
    }

    private void displayStyledDocument(StyledDocument doc) {
        if (textPane.getStyledDocument() != doc) {
            // Remove old listener
            textPane.getStyledDocument().removeDocumentListener(AbstractTextField.this);

            // Replace doc and listener
            textPane.setStyledDocument(doc);
            doc.addDocumentListener(AbstractTextField.this);
        }
    }

    private Range getSelectedTextRange() {
        return Range.ofMarkAndDot(textPane.getCaret().getDot(), textPane.getCaret().getMark());
    }

    private void setActiveTextAlign(Value v) {
        SimpleAttributeSet alignment = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignment, FontUtils.getAlignmentStyleForValue(v));
        textPane.getStyledDocument().setParagraphAttributes(0, textPane.getStyledDocument().getLength(), alignment, false);

        updateModel();
    }

    private void setTextFontFamily(Value fontFamily) {
        Range selection = getSelectedTextRange();
        StyledDocument doc = textPane.getStyledDocument();

        for (int index = selection.start; index < selection.start + selection.length(); index++) {
            TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(doc.getCharacterElement(index).getAttributes());
            tss.setFontFamily(fontFamily.stringValue());
            doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
        }

        updateModel();
        updateFocusedFontSelection();
    }

    private void setTextFontSize(Value fontSize) {
        Range selection = getSelectedTextRange();
        StyledDocument doc = textPane.getStyledDocument();

        for (int index = selection.start; index < selection.start + selection.length(); index++) {
            TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(doc.getCharacterElement(index).getAttributes());
            tss.setFontSize(fontSize.integerValue());
            doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
        }

        updateModel();
        updateFocusedFontSelection();
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
        updateFocusedFontSelection();
    }

    protected void setWideMargins(boolean isWideMargins) {
        if (isWideMargins) {
            textPane.setMargin(new Insets(WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX));
        } else {
            textPane.setMargin(new Insets(NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX));
        }

        textPane.invalidate();
        textPane.repaint();
    }

    private void updateModel() {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setStyledDocument(textPane.getStyledDocument());
    }

    private void updateFocusedFontSelection() {
        Range selection = getSelectedTextRange();

        Value styles = new Value("plain");
        Set<Value> families = new HashSet<>();
        Set<Value> sizes = new HashSet<>();

        if (selection.isEmpty()) {
            AttributeSet attributes = textPane.getCharacterAttributes();
            TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(attributes);
            styles = tss.getHyperTalkStyle();
            families.add(new Value(tss.getFontFamily()));
            sizes.add(new Value(tss.getFontSize()));
        } else {
            for (int thisChar = selection.start; thisChar < selection.end; thisChar++) {
                AttributeSet attributes = textPane.getStyledDocument().getCharacterElement(thisChar).getAttributes();
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(attributes);
                styles = tss.appendStyle(styles);
                families.add(new Value(tss.getFontFamily()));
                sizes.add(new Value(tss.getFontSize()));
            }
        }

        FontContext.getInstance().getFocusedFontFamilyProvider().set(families);
        FontContext.getInstance().getFocusedFontSizeProvider().set(sizes);
        FontContext.getInstance().getFocusedFontStyleProvider().set(styles);
    }

    private class FontStyleObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            if (textPane.hasFocus()) {
                ThreadUtils.invokeAndWaitAsNeeded(() -> setTextFontStyle((Value) arg));
            }
        }
    }

    private class FontSizeObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            if (textPane.hasFocus()) {
                ThreadUtils.invokeAndWaitAsNeeded(() -> setTextFontSize((Value) arg));
            }
        }
    }

    private class FontFamilyObserver implements Observer {
        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Observable o, Object arg) {
            if (textPane.hasFocus()) {
                ThreadUtils.invokeAndWaitAsNeeded(() -> setTextFontFamily((Value) arg));
            }
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

    private class AutoSelectObserver implements MouseListenable, MouseMotionListenable {

        /** {@inheritDoc} */
        @Override
        public void mousePressed(MouseEvent e) {
            if (toolEditablePart.getPartModel().getKnownProperty(FieldModel.PROP_AUTOSELECT).booleanValue()) {
                FieldModel model = (FieldModel) toolEditablePart.getPartModel();
                model.autoSelectLine(textPane.getClickedLine(e), false);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void mouseDragged(MouseEvent e) {
            if (toolEditablePart.getPartModel().getKnownProperty(FieldModel.PROP_AUTOSELECT).booleanValue() &&
                    toolEditablePart.getPartModel().getKnownProperty(FieldModel.PROP_MULTIPLELINES).booleanValue())
            {
                FieldModel model = (FieldModel) toolEditablePart.getPartModel();
                model.autoSelectLine(textPane.getClickedLine(e), true);
            }
        }
    }
}
