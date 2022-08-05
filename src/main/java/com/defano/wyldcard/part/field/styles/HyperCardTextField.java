package com.defano.wyldcard.part.field.styles;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.awt.keyboard.KeyListenable;
import com.defano.wyldcard.awt.mouse.MouseListenable;
import com.defano.wyldcard.awt.mouse.MouseMotionListenable;
import com.defano.wyldcard.border.PartBorderFactory;
import com.defano.wyldcard.font.FontUtils;
import com.defano.wyldcard.font.TextStyleSpecifier;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.part.ToolEditablePart;
import com.defano.wyldcard.part.card.CardPart;
import com.defano.wyldcard.part.field.FieldModel;
import com.defano.wyldcard.part.field.FieldModelObserver;
import com.defano.wyldcard.part.model.PropertyChangeObserver;
import com.defano.wyldcard.part.util.LifecycleObserver;
import com.defano.wyldcard.property.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.thread.Throttle;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.util.Range;
import com.defano.wyldcard.window.layout.FontSizePicker;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import javax.swing.*;
import javax.swing.event.*;
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
public abstract class HyperCardTextField extends JScrollPane implements
        PropertyChangeObserver, DocumentListener, CaretListener, FieldModelObserver, LifecycleObserver
{
    protected static final int WIDE_MARGIN_PX = 6;
    protected static final int NARROW_MARGIN_PX = 1;

    private final transient HyperCardTextPane textPane;
    private final transient ToolModeObserver toolModeObserver = new ToolModeObserver();
    private final transient FontAlignObserver fontAlignObserver = new FontAlignObserver();
    private final transient FontSizeObserver fontSizeObserver = new FontSizeObserver();
    private final transient FontStyleObserver fontStyleObserver = new FontStyleObserver();
    private final transient FontFamilyObserver fontFamilyObserver = new FontFamilyObserver();
    private final transient AutoTabKeyObserver autoTabKeyObserver = new AutoTabKeyObserver();
    private final transient AutoSelectObserver autoSelectObserver = new AutoSelectObserver();
    private final transient ScrollObserver scrollObserver = new ScrollObserver();
    private final transient FocusObserver focusObserver = new FocusObserver();
    private final transient ViewportChangeListener viewportChangeListener = new ViewportChangeListener();

    private transient Disposable toolModeSubscription;
    private transient Disposable fontSizeSubscription;
    private transient Disposable fontStyleSubscription;
    private transient Disposable fontFamilySubscription;
    private transient Disposable fontAlignSubscription;

    private final transient ToolEditablePart toolEditablePart;
    private static final Throttle fontSelectionThrottle = new Throttle("font-selection-throttle", 500);

    // Non-Mac L&Fs cause focus to be lost when user clicks on menu bar; this boolean overrides that behavior so that
    // menu remains useful for text property changes.
    private boolean didLoseFocusToMenu = false;

    public HyperCardTextField(ToolEditablePart toolEditablePart) {
        this.toolEditablePart = toolEditablePart;

        // Create the editor component
        textPane = new HyperCardTextPane(new DefaultStyledDocument(), getViewport());
        textPane.setEditorKit(new RTFEditorKit());

        this.setViewportView(textPane);
        textPane.setBorder(PartBorderFactory.createEmptyBorder());
    }

    public ToolEditablePart getToolEditablePart() {
        return toolEditablePart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        toolEditablePart.getPartModel().addPropertyChangedObserver(this);
        getViewport().addChangeListener(viewportChangeListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
        toolEditablePart.getPartModel().removePropertyChangedObserver(this);
        getViewport().removeChangeListener(viewportChangeListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void insertUpdate(DocumentEvent e) {
        documentUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void removeUpdate(DocumentEvent e) {
        documentUpdate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void changedUpdate(DocumentEvent e) {
        documentUpdate();
    }

    private void documentUpdate() {
        syncModelToView(new ExecutionContext());
        textPane.invalidateViewport(getViewport());
        this.textPane.setDirty(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void paint(Graphics g) {
        super.paint(g);
        toolEditablePart.drawSelectionRectangle(g);
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public boolean hasFocus() {
        return didLoseFocusToMenu || (textPane != null && textPane.hasFocus());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case FieldModel.PROP_DONTWRAP:
                textPane.setWrapText(!newValue.booleanValue());
                break;

            case FieldModel.PROP_LOCKTEXT:
                textPane.setLockText(newValue.booleanValue());
                break;

            case FieldModel.PROP_SHOWLINES:
                textPane.setShowLines(newValue.booleanValue());
                break;

            case FieldModel.PROP_TEXTALIGN:
                setActiveTextAlign(context, newValue);
                break;

            case FieldModel.PROP_ENABLED:
                toolEditablePart.setEnabledOnCard(context, newValue.booleanValue());
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

            default:
                // Nothing to do
        }
    }

    @RunOnDispatch
    public HyperCardTextPane getTextPane() {
        return textPane;
    }

    @RunOnDispatch
    public void setEditable(boolean editable) {
        super.setEnabled(editable);
        textPane.setEditable(editable);
    }

    @RunOnDispatch
    public void partOpened(ExecutionContext context) {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();

        // Get notified when field tool is selected
        toolModeSubscription = WyldCard.getInstance().getPaintManager().getToolModeProvider().subscribe(toolModeObserver);

        fontAlignSubscription = WyldCard.getInstance().getFontManager().getSelectedTextAlignProvider().subscribe(fontAlignObserver);
        fontFamilySubscription = WyldCard.getInstance().getFontManager().getSelectedFontFamilyProvider().subscribe(fontFamilyObserver);
        fontStyleSubscription = WyldCard.getInstance().getFontManager().getSelectedFontStyleProvider().subscribe(fontStyleObserver);
        fontSizeSubscription = WyldCard.getInstance().getFontManager().getSelectedFontSizeProvider().subscribe(fontSizeObserver);

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
        displayStyledDocument(model.getStyledDocument(context));
        toolEditablePart.getPartModel().notifyPropertyChangedObserver(context, this, true);

        // Initialize font to system font selection if document is empty
        if (textPane.getText().length() == 0) {
            textPane.setCharacterAttributes(WyldCard.getInstance().getFontManager().getFocusedTextStyle().toAttributeSet(), true);
        }

        // And auto-select any
        SwingUtilities.invokeLater(() -> textPane.autoSelectLines(model.getAutoSelectedLines(context)));
    }

    @RunOnDispatch
    public void partClosed(ExecutionContext context) {
        syncModelToView(context);
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
        fontAlignSubscription.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void caretUpdate(CaretEvent e) {
        updateSelectedLoc();

        // Update selectedText
        FieldModel fieldModel = (FieldModel) toolEditablePart.getPartModel();
        if (fieldModel.isAutoSelection(new ExecutionContext())) {
            fieldModel.updateSelectionContext(new ExecutionContext(), getSelectedTextRange(), false);
        } else {
            fieldModel.updateSelectionContext(new ExecutionContext(), Range.ofMarkAndDot(e.getDot(), e.getMark()), true);
        }

        // Update font style selection in menus
        if (textPane.getText().length() > 0 && textPane.isEditable()) {
            updateSelection();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onStyledDocumentChanged(StyledDocument document) {
        displayStyledDocument(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onAutoSelectionChanged(Set<Integer> selectedLines) {
        textPane.autoSelectLines(selectedLines);
        updateSelection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onSelectionChange(Range selection) {
        textPane.requestFocus();
        try {
            textPane.setSelectionStart(selection.start);
            textPane.setSelectionEnd(selection.end);
        } catch (Exception t) {
            textPane.paintSelection(selection);
        }
        updateSelection();
    }

    /**
     * Sets the scrolled amount of this field to the given value. This value represents the number of pixels that have
     * scrolled above the top of the text field.
     *
     * @param scroll The amount to scroll.
     */
    @RunOnDispatch
    public void setScroll(int scroll) {
        getVerticalScrollBar().setValue(scroll);
    }

    /**
     * Gets the scrolled amount of this pixel.
     * @return The number of pixels that have scrolled above the top of this field
     */
    @RunOnDispatch
    public int getScroll() {
        return getVerticalScrollBar().getValue();
    }


    @RunOnDispatch
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

    @RunOnDispatch
    private Range getSelectedTextRange() {
        return Range.ofMarkAndDot(textPane.getCaret().getDot(), textPane.getCaret().getMark());
    }

    @RunOnDispatch
    private void setActiveTextAlign(ExecutionContext context, Value v) {
        SimpleAttributeSet alignment = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignment, FontUtils.getAlignmentStyleForValue(v));
        textPane.getStyledDocument().setParagraphAttributes(0, textPane.getStyledDocument().getLength(), alignment, false);

        syncModelToView(context);
    }

    @RunOnDispatch
    protected void setWideMargins(boolean isWideMargins) {
        if (isWideMargins) {
            textPane.setMargin(new Insets(WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX, WIDE_MARGIN_PX));
        } else {
            textPane.setMargin(new Insets(NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX, NARROW_MARGIN_PX));
        }

        textPane.invalidate();
        textPane.repaint();
    }

    @RunOnDispatch
    private void syncModelToView(ExecutionContext context) {
        FieldModel model = (FieldModel) toolEditablePart.getPartModel();
        model.setStyledDocument(context, textPane.getStyledDocument());
    }

    @RunOnDispatch
    private void updateSelectedLoc() {
        Point caretPos = textPane.getCaret().getMagicCaretPosition();
        Component cardPart = SwingUtilities.getAncestorOfClass(CardPart.class, textPane);

        if (caretPos != null && cardPart != null) {
            Point cardLoc = SwingUtilities.convertPoint(textPane, caretPos, cardPart);
            WyldCard.getInstance().getSelectionManager().setSelectedLoc(new Value(cardLoc));
        }
    }

    /**
     * Update the menu bar font style selection (Font and Style menus) with the font, size and style of the active
     * text selection.
     */
    @RunOnDispatch
    private void updateSelection() {

        // Style calculation can be costly for large selections; throttle repeated requests in the background
        fontSelectionThrottle.submitOnUiThread(hashCode(), () -> {
            Range selection = getSelectedTextRange();

            Set<Value> styles = new HashSet<>();
            Set<Value> families = new HashSet<>();
            Set<Value> sizes = new HashSet<>();
            Value alignment = new Value("left");

            // No selection; aggregate and report styles of entire field
            if (selection.isEmpty()) {
                AttributeSet attributes = textPane.getCharacterAttributes();
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(attributes);

                alignment = new Value(tss.getAlign());
                styles.add(tss.getHyperTalkStyle());
                families.add(new Value(tss.getFontFamily()));
                sizes.add(new Value(tss.getFontSize()));
            }

            // Selection exists; aggregate and report styles only of selected text
            else {
                for (int thisChar = selection.start; thisChar < selection.end; thisChar++) {
                    AttributeSet attributes = textPane.getStyledDocument().getCharacterElement(thisChar).getAttributes();
                    TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(attributes);

                    alignment = new Value(tss.getAlign());
                    styles.add(tss.getHyperTalkStyle());
                    families.add(new Value(tss.getFontFamily()));
                    sizes.add(new Value(tss.getFontSize()));
                }
            }

            WyldCard.getInstance().getFontManager().getFocusedFontFamilyProvider().onNext(families);
            WyldCard.getInstance().getFontManager().getFocusedFontSizeProvider().onNext(sizes);
            WyldCard.getInstance().getFontManager().setFocusedHyperTalkFontStyles(styles);
            WyldCard.getInstance().getFontManager().setFocusedTextAlign(alignment);
        });
    }

    private class FontStyleObserver implements Consumer<Value> {
        @Override
        public void accept(Value style) {
            if (hasFocus()) {
                Invoke.onDispatch(() -> setTextFontStyle(new ExecutionContext(), style));
            }
        }

        @RunOnDispatch
        private void setTextFontStyle(ExecutionContext context, Value fontStyle) {
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

            syncModelToView(context);
            updateSelection();
        }
    }

    private class FontAlignObserver implements Consumer<Value> {
        @Override
        public void accept(Value align) {
            if (hasFocus()) {
                Invoke.onDispatch(() -> setActiveTextAlign(new ExecutionContext(), align));
            }
        }
    }

    private class FontSizeObserver implements Consumer<Value> {
        @Override
        public void accept(Value size) {
            if (hasFocus()) {
                Invoke.onDispatch(() -> setTextFontSize(new ExecutionContext(), size));
            }
        }

        @RunOnDispatch
        private void setTextFontSize(ExecutionContext context, Value fontSize) {
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

            syncModelToView(context);
            updateSelection();
        }
    }

    private class FontFamilyObserver implements Consumer<Value> {
        @Override
        public void accept(Value family) {
            if (hasFocus()) {
                Invoke.onDispatch(() -> setTextFontFamily(new ExecutionContext(), family));
            }
        }

        @RunOnDispatch
        private void setTextFontFamily(ExecutionContext context, Value fontFamily) {
            Range selection = getSelectedTextRange();
            StyledDocument doc = textPane.getStyledDocument();

            // Apply change to field
            if (selection.length() == 0) {
                TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(textPane.getCharacterAttributes());
                tss.setFontFamily(fontFamily.toString());
                textPane.setCharacterAttributes(tss.toAttributeSet(), true);
            }

            // Apply change to highlighted text
            else {
                for (int index = selection.start; index < selection.start + selection.length(); index++) {
                    TextStyleSpecifier tss = TextStyleSpecifier.fromAttributeSet(doc.getCharacterElement(index).getAttributes());
                    tss.setFontFamily(fontFamily.toString());
                    doc.setCharacterAttributes(index, 1, tss.toAttributeSet(), true);
                }
            }

            syncModelToView(context);
            updateSelection();
        }
    }

    private class ToolModeObserver implements Consumer<ToolMode> {
        @Override
        public void accept(ToolMode toolMode) {
            Invoke.onDispatch(() -> {
                textPane.setFocusable(ToolMode.BROWSE == toolMode);

                setHorizontalScrollBarPolicy(ToolMode.FIELD == toolMode ? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER : ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                setVerticalScrollBarPolicy(ToolMode.FIELD == toolMode ? ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER : ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                setEditable(ToolMode.FIELD != toolMode && !toolEditablePart.getPartModel().get(new ExecutionContext(), FieldModel.PROP_LOCKTEXT).booleanValue());
            });
        }
    }

    private class AutoTabKeyObserver implements KeyListenable {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_TAB &&
                    toolEditablePart.getPartModel().get(new ExecutionContext(), FieldModel.PROP_AUTOTAB).booleanValue())
            {
                e.consume();
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            }
        }
    }

    private class AutoSelectObserver implements MouseListenable, MouseMotionListenable {
        @Override
        @RunOnDispatch
        public void mousePressed(MouseEvent e) {
            if (toolEditablePart.getPartModel().get(new ExecutionContext(), FieldModel.PROP_AUTOSELECT).booleanValue()) {
                FieldModel model = (FieldModel) toolEditablePart.getPartModel();
                model.autoSelectLine(new ExecutionContext(), textPane.getClickedLine(e), false);
            }
        }

        @Override
        @RunOnDispatch
        public void mouseDragged(MouseEvent e) {
            if (toolEditablePart.getPartModel().get(new ExecutionContext(), FieldModel.PROP_AUTOSELECT).booleanValue() &&
                    toolEditablePart.getPartModel().get(new ExecutionContext(), FieldModel.PROP_MULTIPLELINES).booleanValue())
            {
                FieldModel model = (FieldModel) toolEditablePart.getPartModel();
                model.autoSelectLine(new ExecutionContext(), textPane.getClickedLine(e), true);
            }
        }
    }

    private class ScrollObserver implements AdjustmentListener {
        @Override
        @RunOnDispatch
        public void adjustmentValueChanged(AdjustmentEvent e) {
            toolEditablePart.getPartModel().setQuietly(new ExecutionContext(), FieldModel.PROP_SCROLL, new Value(e.getValue()));
        }
    }

    private class FocusObserver implements FocusListener {
        @Override
        @RunOnDispatch
        public void focusGained(FocusEvent e) {
            didLoseFocusToMenu = false;
        }

        @Override
        @RunOnDispatch
        public void focusLost(FocusEvent e) {
            didLoseFocusToMenu = e.getOppositeComponent() != null &&
                    (e.getOppositeComponent() instanceof JRootPane ||
                    SwingUtilities.getWindowAncestor(e.getOppositeComponent()) instanceof FontSizePicker);
        }
    }

    private class ViewportChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            textPane.invalidateViewport(getViewport());
        }
    }

}
