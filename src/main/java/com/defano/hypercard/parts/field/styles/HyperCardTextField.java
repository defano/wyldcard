package com.defano.hypercard.parts.field.styles;

import com.defano.hypercard.awt.KeyListenable;
import com.defano.hypercard.awt.MouseListenable;
import com.defano.hypercard.awt.MouseMotionListenable;
import com.defano.hypercard.fonts.FontUtils;
import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.field.FieldComponent;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.field.FieldModelObserver;
import com.defano.hypercard.parts.model.PropertiesModel;
import com.defano.hypercard.runtime.context.FontContext;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypercard.util.Throttle;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.utils.Range;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

/**
 * An abstract HyperCard text field. That is, a field without a specific style bound to it. Encapsulates the styleable,
 * editable text component and the scrollable surface in which it is embedded.
 */
public abstract class HyperCardTextField extends JScrollPane implements FieldComponent, DocumentListener, CaretListener, FieldModelObserver {

    public final static int WIDE_MARGIN_PX = 15;
    public final static int NARROW_MARGIN_PX = 1;

    private final HyperCardTextPane textPane;
    private final ToolModeObserver toolModeObserver = new ToolModeObserver();
    private final FontSizeObserver fontSizeObserver = new FontSizeObserver();
    private final FontStyleObserver fontStyleObserver = new FontStyleObserver();
    private final FontFamilyObserver fontFamilyObserver = new FontFamilyObserver();
    private final AutoTabKeyObserver autoTabKeyObserver = new AutoTabKeyObserver();
    private final AutoSelectObserver autoSelectObserver = new AutoSelectObserver();
    private final ScrollObserver scrollObserver = new ScrollObserver();
    private final FocusObserver focusObserver = new FocusObserver();

    private Disposable toolModeSubscription;
    private Disposable fontSizeSubscription;
    private Disposable fontStyleSubscription;
    private Disposable fontFamilySubscription;
    private Disposable autoTabKeySubscription;
    private Disposable autoSelectSubscription;
    private Disposable scrollSubscription;
    private Disposable focusSubscription;

    private final ToolEditablePart toolEditablePart;
    private final Throttle fontSelectionThrottle = new Throttle(500);

    // Non-Mac L&Fs cause focus to be lost when user clicks on menu bar; this boolean overrides that behavior so that
    // menu remains useful for text property changes
    private boolean didLoseFocusToMenu = false;

    public HyperCardTextField(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        // Create the editor component
        textPane = new HyperCardTextPane(new DefaultStyledDocument());
        textPane.setEditorKit(new RTFEditorKit());

        this.setViewportView(textPane);

        getViewport().addChangeListener(e -> {
            textPane.invalidateViewport(getViewport());
        });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        enqueueModelUpdateRequest();
        textPane.invalidateViewport(getViewport());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        enqueueModelUpdateRequest();
        textPane.invalidateViewport(getViewport());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        enqueueModelUpdateRequest();
        textPane.invalidateViewport(getViewport());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasFocus() {
        return didLoseFocusToMenu || (textPane != null && textPane.hasFocus());
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

            case FieldModel.PROP_SCROLL:
                setScroll(newValue.integerValue());
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HyperCardTextPane getTextPane() {
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
        ToolsContext.getInstance().getToolModeProvider().subscribe(toolModeObserver);

        FontContext.getInstance().getSelectedFontFamilyProvider().subscribe(fontFamilyObserver);
        FontContext.getInstance().getSelectedFontStyleProvider().subscribe(fontStyleObserver);
        FontContext.getInstance().getSelectedFontSizeProvider().subscribe(fontSizeObserver);

        // React to scripted changes to the field model
        ((FieldModel) toolEditablePart.getPartModel()).setDocumentObserver(this);

        // Add mouse and keyboard listeners
        textPane.addMouseListener(toolEditablePart);
        textPane.addCaretListener(this);
        textPane.addKeyListener(autoTabKeyObserver);
        textPane.addMouseListener(autoSelectObserver);
        textPane.addMouseMotionListener(autoSelectObserver);
        textPane.addFocusListener(focusObserver);

        getVerticalScrollBar().addAdjustmentListener(scrollObserver);

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
        enqueueModelUpdateRequest();
        textPane.getStyledDocument().removeDocumentListener(this);

        textPane.removeMouseListener(toolEditablePart);
        textPane.removeCaretListener(this);
        textPane.addKeyListener(autoTabKeyObserver);
        textPane.removeMouseListener(autoSelectObserver);
        textPane.removeFocusListener(focusObserver);

        getVerticalScrollBar().removeAdjustmentListener(scrollObserver);

        toolEditablePart.getPartModel().removePropertyChangedObserver(this);
        ((FieldModel) toolEditablePart.getPartModel()).setDocumentObserver(null);

        fontFamilySubscription.dispose();
        fontStyleSubscription.dispose();
        fontSizeSubscription.dispose();
        toolModeSubscription.dispose();
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
    public void onAutoSelectionChanged(Set<Integer> selectedLines) {
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

    /**
     * Sets the scrolled amount of this field to the given value. This value represents the number of pixels that have
     * scrolled above the top of the text field.
     *
     * @param scroll The amount to scroll.
     */
    public void setScroll(int scroll) {
        getVerticalScrollBar().setValue(scroll);
    }

    /**
     * Gets the scrolled amount of this pixel.
     * @return The number of pixels that have scrolled above the top of this field
     */
    public int getScroll() {
        return getVerticalScrollBar().getValue();
    }


    private void displayStyledDocument(StyledDocument doc) {
        if (textPane.getStyledDocument() != doc) {
            int oldCaretPosition = textPane.getCaretPosition();

            // Remove old listener
            textPane.getStyledDocument().removeDocumentListener(HyperCardTextField.this);

            // Replace doc and listener
            textPane.setStyledDocument(doc);
            doc.addDocumentListener(HyperCardTextField.this);

            textPane.setCaretPosition(oldCaretPosition);
        }
    }

    private Range getSelectedTextRange() {
        return Range.ofMarkAndDot(textPane.getCaret().getDot(), textPane.getCaret().getMark());
    }

    private void setActiveTextAlign(Value v) {
        SimpleAttributeSet alignment = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignment, FontUtils.getAlignmentStyleForValue(v));
        textPane.getStyledDocument().setParagraphAttributes(0, textPane.getStyledDocument().getLength(), alignment, false);

        enqueueModelUpdateRequest();
    }

    private void setTextFontFamily(Value fontFamily) {
        Range selection = getSelectedTextRange();
        StyledDocument doc = textPane.getStyledDocument();

        // Apply change to field
        if (selection.length() == 0) {
            TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(textPane.getCharacterAttributes());
            tss.setFontFamily(fontFamily.stringValue());
            textPane.setCharacterAttributes(tss.toAttributeSet(), true);
        }

        // Apply change to highlighted text
        else {
            for (int index = selection.start; index < selection.start + selection.length(); index++) {
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(doc.getCharacterElement(index).getAttributes());
                tss.setFontFamily(fontFamily.stringValue());
                doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
            }
        }

        enqueueModelUpdateRequest();
        updateFocusedFontSelection();
    }

    private void setTextFontSize(Value fontSize) {
        Range selection = getSelectedTextRange();
        StyledDocument doc = textPane.getStyledDocument();

        // Apply change to field
        if (selection.length() == 0) {
            TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(textPane.getCharacterAttributes());
            tss.setFontSize(fontSize.integerValue());
            textPane.setCharacterAttributes(tss.toAttributeSet(), true);
        }

        // Apply change to highlighted text
        else {
            for (int index = selection.start; index < selection.start + selection.length(); index++) {
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(doc.getCharacterElement(index).getAttributes());
                tss.setFontSize(fontSize.integerValue());
                doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
            }
        }

        enqueueModelUpdateRequest();
        updateFocusedFontSelection();
    }

    private void setTextFontStyle(Value fontStyle) {
        Range selection = getSelectedTextRange();
        StyledDocument doc = textPane.getStyledDocument();

        // Apply change to field
        if (selection.length() == 0) {
            TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(textPane.getCharacterAttributes());
            tss.setFontStyle(fontStyle);
            textPane.setCharacterAttributes(tss.toAttributeSet(), true);
        }

        // Apply change to highlighted text
        else {
            for (int index = selection.start; index < selection.start + selection.length(); index++) {
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(doc.getCharacterElement(index).getAttributes());
                tss.setFontStyle(fontStyle);
                doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
            }
        }

        enqueueModelUpdateRequest();
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

    private void enqueueModelUpdateRequest() {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setStyledDocument(textPane.getStyledDocument());
    }

    /**
     * Update the menu bar font style selection (Font and Style menus) with the font, size and style of the active
     * text selection.
     */
    private void updateFocusedFontSelection() {

        // Style calculation can be costly for large selections; throttle repeated requests in the background
        fontSelectionThrottle.submit(() -> {
            Range selection = getSelectedTextRange();

            Set<Value> styles = new HashSet<>();
            Set<Value> families = new HashSet<>();
            Set<Value> sizes = new HashSet<>();

            // No selection; aggregate and report styles of entire field
            if (selection.isEmpty()) {
                AttributeSet attributes = textPane.getCharacterAttributes();
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(attributes);
                styles.add(tss.getHyperTalkStyle());
                families.add(new Value(tss.getFontFamily()));
                sizes.add(new Value(tss.getFontSize()));
            }

            // Selection exists; aggregate and report styles only of selected text
            else {
                for (int thisChar = selection.start; thisChar < selection.end; thisChar++) {
                    AttributeSet attributes = textPane.getStyledDocument().getCharacterElement(thisChar).getAttributes();
                    TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(attributes);
                    styles.add(tss.getHyperTalkStyle());
                    families.add(new Value(tss.getFontFamily()));
                    sizes.add(new Value(tss.getFontSize()));
                }
            }

            FontContext.getInstance().getFocusedFontFamilyProvider().onNext(families);
            FontContext.getInstance().getFocusedFontSizeProvider().onNext(sizes);
            FontContext.getInstance().setFocusedHyperTalkFontStyles(styles);
        });
    }

    private class FontStyleObserver implements Consumer<Value> {
        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(Value style) {
            if (hasFocus()) {
                ThreadUtils.invokeAndWaitAsNeeded(() -> setTextFontStyle(style));
            }
        }
    }

    private class FontSizeObserver implements Consumer<Value> {
        @Override
        public void accept(Value size) {
            if (hasFocus()) {
                ThreadUtils.invokeAndWaitAsNeeded(() -> setTextFontSize(size));
            }
        }
    }

    private class FontFamilyObserver implements Consumer<Value> {
        @Override
        public void accept(Value family) {
            if (hasFocus()) {
                ThreadUtils.invokeAndWaitAsNeeded(() -> setTextFontFamily(family));
            }
        }
    }

    private class ToolModeObserver implements Consumer<ToolMode> {
        @Override
        public void accept(ToolMode toolMode) {
            textPane.setFocusable(ToolMode.BROWSE == toolMode);

            setHorizontalScrollBarPolicy(ToolMode.FIELD == toolMode ? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER : ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            setVerticalScrollBarPolicy(ToolMode.FIELD == toolMode ? ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER : ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            setEditable(ToolMode.FIELD != toolMode && !toolEditablePart.getPartModel().getKnownProperty(FieldModel.PROP_LOCKTEXT).booleanValue());
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

    private class ScrollObserver implements AdjustmentListener {
        /** {@inheritDoc} */
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            toolEditablePart.getPartModel().setKnownProperty(FieldModel.PROP_SCROLL, new Value(e.getValue()), true);
        }
    }

    private class FocusObserver implements FocusListener {
        /** {@inheritDoc} */
        @Override
        public void focusGained(FocusEvent e) {
            didLoseFocusToMenu = false;
        }

        /** {@inheritDoc} */
        @Override
        public void focusLost(FocusEvent e) {
            didLoseFocusToMenu = e.getOppositeComponent() instanceof JRootPane;
        }
    }

}
