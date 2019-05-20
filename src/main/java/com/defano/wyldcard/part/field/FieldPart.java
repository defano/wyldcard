package com.defano.wyldcard.part.field;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.enums.ArrowDirection;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.ast.model.enums.ToolType;
import com.defano.hypertalk.util.Range;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.awt.keyboard.DeferredKeyEvent;
import com.defano.wyldcard.awt.mouse.MouseStillDown;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.message.MessageBuilder;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.part.ToolEditablePart;
import com.defano.wyldcard.part.builder.FieldModelBuilder;
import com.defano.wyldcard.part.card.CardLayerPart;
import com.defano.wyldcard.part.card.CardLayerPartModel;
import com.defano.wyldcard.part.card.CardPart;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.part.model.PropertyChangeObserver;
import com.defano.wyldcard.part.util.FieldUtilities;
import com.defano.wyldcard.part.util.TextArrowsMessageCompletionObserver;
import com.defano.wyldcard.property.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;

/**
 * The controller object associated with a field on the card.
 * <p>
 * See {@link FieldModel} for the model object associated with this controller.
 * See {@link StyleableField} for the view object associated with this view.
 */
public class FieldPart extends StyleableField implements CardLayerPart<FieldModel>, Searchable, PropertyChangeObserver, DeferredKeyEventListener, FocusListener {

    public static final int DEFAULT_WIDTH = 250;
    public static final int DEFAULT_HEIGHT = 100;

    private final Owner owner;
    private final WeakReference<CardPart> parent;
    private FieldModel partModel;

    private FieldPart(FieldStyle style, CardPart parent, Owner owner) {
        super(style);

        this.owner = owner;
        this.parent = new WeakReference<>(parent);
    }

    /**
     * Creates a new field with default attributes on the given card.
     *
     * @param context   The execution context.
     * @param parent    The card in which the field should be generated.
     * @param rectangle The size and location of the field on the card; when null, a default sized field will be placed
     *                  in the center of the card.
     * @return The newly created FieldPart
     */
    public static FieldPart newField(ExecutionContext context, CardPart parent, Owner owner, Rectangle rectangle) {
        FieldPart newField = new FieldPart(FieldStyle.TRANSPARENT, parent, owner);

        // When bounds are specified, place default-sized field in center of the card
        if (rectangle == null) {
            rectangle = new Rectangle(
                    parent.getWidth() / 2 - (DEFAULT_WIDTH / 2),
                    parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2),
                    DEFAULT_WIDTH,
                    DEFAULT_HEIGHT);
        }

        newField.initProperties(rectangle, parent.getPartModel());
        newField.partModel.set(context, FieldModel.PROP_TEXTFONT, new Value(WyldCard.getInstance().getFontManager().getFocusedTextStyle().getFontFamily()));
        newField.partModel.set(context, FieldModel.PROP_TEXTSIZE, new Value(WyldCard.getInstance().getFontManager().getFocusedTextStyle().getFontSize()));
        newField.partModel.set(context, FieldModel.PROP_TEXTSTYLE, WyldCard.getInstance().getFontManager().getFocusedTextStyle().getHyperTalkStyle());

        return newField;
    }

    /**
     * Creates a new field from an existing field data model.
     *
     * @param context The execution context.
     * @param parent  The card in which the field should be created.
     * @param model   The data model of the field to be created.
     * @return The newly created field.
     */
    public static FieldPart fromModel(ExecutionContext context, CardPart parent, FieldModel model) {
        FieldPart field = new FieldPart(FieldStyle.fromName(model.get(context, FieldModel.PROP_STYLE).toString()), parent, model.getOwner());

        model.setCurrentCardId(parent.getId(context));
        field.partModel = model;

        return field;
    }

    @Override
    public ToolEditablePart<FieldModel> getToolEditablePart() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSearchHilites() {
        getHyperCardTextPane().clearSearchHilights();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applySearchHilite(Range range) {
        getHyperCardTextPane().applySearchHilight(range);
    }

    @Override
    public String getText(ExecutionContext context) {
        return getPartModel().getText(context);
    }

    /**
     * {@inheritDoc}
     *
     * @param context
     */
    @Override
    public void partClosed(ExecutionContext context) {
        super.partClosed(context);

        partModel.removePropertyChangedObserver(this);
        getHyperCardTextPane().removeFocusListener(this);
    }

    /**
     * {@inheritDoc}
     *
     * @param context
     */
    @Override
    public void partOpened(ExecutionContext context) {
        super.partOpened(context);

        partModel.addPropertyChangedObserver(this);
        getHyperCardTextPane().addFocusListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ToolType getEditTool() {
        return ToolType.FIELD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceViewComponent(ExecutionContext context, Component oldComponent, Component newComponent) {
        CardPart part = parent.get();
        if (part != null) {
            part.replaceViewComponent(context, this, oldComponent, newComponent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartType getType() {
        return PartType.FIELD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getComponent() {
        return this.getFieldComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardLayerPart getPart() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldModel getPartModel() {
        return partModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPartToolActive() {
        return WyldCard.getInstance().getPaintManager().getToolMode() == ToolMode.FIELD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart getCard() {
        return parent.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (SwingUtilities.isLeftMouseButton(e) && !isPartToolActive()) {
            // Update the clickText property
            setClickText(e);
            setClickLine(e);
            setClickChunk(e);

            getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_DOWN);
            MouseStillDown.then(() -> getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_STILL_DOWN));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        boolean isStillInFocus = new Rectangle(this.getFieldComponent().getSize()).contains(e.getPoint());

        // Do not set mouseUp if cursor is not released while over the part
        if (SwingUtilities.isLeftMouseButton(e) && isStillInFocus && !isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_UP);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);

        if (!isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_ENTER);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);

        if (!isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_LEAVE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        if (e.getClickCount() == 2 && !isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_DOUBLE_CLICK);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);

        if (getHyperCardTextPane().hasFocus() && e.getWhen() > 0 && !isPartToolActive()) {
            getPartModel().receiveAndDeferKeyEvent(
                    new ExecutionContext(this),
                    MessageBuilder.named(SystemMessage.KEY_DOWN.messageName).withArgument(e.getKeyChar()).build(),
                    e,
                    this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        // Special case: When textArrows is false, arrow keys navigate between cards even when field has focus
        if (!WyldCard.getInstance().getWyldCardPart().isTextArrows() &&
                e.getID() == KeyEvent.KEY_PRESSED &&
                ArrowDirection.fromKeyEvent(e) != null) {
            new TextArrowsMessageCompletionObserver(getCard(), e).doArrowKeyNavigation();
        }

        // Key press didn't result in arrow key navigation, let field process event
        else if (getHyperCardTextPane().hasFocus() && !isPartToolActive()) {
            Message msg = SystemMessage.fromKeyEvent(e, true);
            ExecutionContext context = new ExecutionContext(this);

            // EnterInField message should be dispatched even during redispatchInProgress
            if (msg != null && (e.getWhen() > 0 || msg.getMessageName().equalsIgnoreCase(SystemMessage.ENTER_IN_FIELD.getMessageName()))) {
                getPartModel().receiveAndDeferKeyEvent(context, msg, e, this);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case FieldModel.PROP_STYLE:
                setStyle(context, FieldStyle.fromName(newValue.toString()));
                break;
            case FieldModel.PROP_TOP:
            case FieldModel.PROP_LEFT:
            case FieldModel.PROP_WIDTH:
            case FieldModel.PROP_HEIGHT:
                getComponent().setBounds(partModel.getRect(context));
                getComponent().validate();
                getComponent().repaint();
                break;
            case FieldModel.PROP_VISIBLE:
                setVisibleWhenBrowsing(context, newValue.booleanValue());
                break;
            case CardLayerPartModel.PROP_ZORDER:
                getCard().invalidatePartsZOrder(context);
                break;
        }
    }

    private void setClickLine(MouseEvent evt) {
        int clickIndex = getHyperCardTextPane().viewToModel(evt.getPoint());
        int clickLine = FieldUtilities.getLineOfChar(clickIndex, getText(new ExecutionContext(this)));

        WyldCard.getInstance().getSelectionManager().setClickLine(new Value(clickLine));
    }

    private void setClickChunk(MouseEvent evt) {
        try {
            int clickIndex = getHyperCardTextPane().viewToModel(evt.getPoint());
            int startWordIndex = Utilities.getWordStart(getHyperCardTextPane(), clickIndex);
            int endWordIndex = Utilities.getWordEnd(getHyperCardTextPane(), clickIndex);

            WyldCard.getInstance().getSelectionManager().setClickChunk(new Value(
                    "chars " + startWordIndex + " to " + endWordIndex + " of " +
                            getPartModel().getHyperTalkAddress(new ExecutionContext(this)))
            );
        } catch (BadLocationException e) {
            // Nothing to do
        }

    }

    private void setClickText(MouseEvent evt) {
        try {
            int clickIndex = getHyperCardTextPane().viewToModel(evt.getPoint());
            int startWordIndex = Utilities.getWordStart(getHyperCardTextPane(), clickIndex);
            int endWordIndex = Utilities.getWordEnd(getHyperCardTextPane(), clickIndex);

            String clickText = getHyperCardTextPane().getStyledDocument().getText(startWordIndex, endWordIndex - startWordIndex);
            WyldCard.getInstance().getSelectionManager().setClickText(new Value(clickText));

        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    private void initProperties(Rectangle geometry, PartModel parentPartModel) {
        CardPart cardPart = parent.get();

        if (cardPart != null) {
            int id = cardPart.getPartModel().getStackModel().getNextFieldId(parentPartModel.getId());
            partModel = new FieldModelBuilder(owner, parentPartModel).withId(id).withBounds(geometry).build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchEvent(DeferredKeyEvent event) {

        // Special case: Enter in field should cause field to lose focus without adding a newline
        if (FieldUtilities.isEnterKeyEvent(event)) {
            Invoke.onDispatch(() -> getCard().requestFocusInWindow());
        } else {
            Invoke.onDispatch(() -> {
                try {
                    getHyperCardTextPane().dispatchEvent(event);
                } catch (Exception e) {
                    // Ignore; may be thrown when sending selection-impacting fields that have locktext enabled
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusGained(FocusEvent e) {
        if (getHyperCardTextPane().isEditable() && e.getOppositeComponent() != null) {
            getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.OPEN_FIELD);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusLost(FocusEvent e) {
        if (getHyperCardTextPane().isEditable()) {
            getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.EXIT_FIELD);
        }
    }
}
