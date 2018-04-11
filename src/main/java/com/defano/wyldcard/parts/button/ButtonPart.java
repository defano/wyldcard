package com.defano.wyldcard.parts.button;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.awt.MouseStillDown;
import com.defano.wyldcard.parts.card.CardLayerPart;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.runtime.PeriodicMessageManager;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.interpreter.Interpreter;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.ref.WeakReference;

/**
 * The controller object associated with a button on a card.
 *
 * See {@link ButtonModel} for the model associated with this controller.
 * See {@link StyleableButton} for the view associated with this controller.
 */
public class ButtonPart extends StyleableButton implements CardLayerPart, MouseListener, PropertyChangeObserver {

    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_HEIGHT = 30;

    private final Owner owner;
    private ButtonModel partModel;
    private final WeakReference<CardPart> parent;

    private ButtonPart(ButtonStyle style, CardPart parent, Owner owner) {
        super(style);

        this.owner = owner;
        this.parent = new WeakReference<>(parent);
    }

    /**
     * Creates a new button on the given card with default size and position.
     *
     *
     * @param context The execution context.
     * @param parent The card that this button will belong to.
     * @return The new button.
     */
    public static ButtonPart newButton(ExecutionContext context, CardPart parent, Owner owner) {
        return newButton(context, parent, owner, new Rectangle(parent.getWidth() / 2 - (DEFAULT_WIDTH / 2), parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2), DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    /**
     * Creates a new button on the given card with a given geometry.
     *
     *
     * @param context The execution context.
     * @param parent The card that this button will belong to.
     * @return The new button.
     */
    public static ButtonPart newButton(ExecutionContext context, CardPart parent, Owner owner, Rectangle rectangle) {
        return fromGeometry(context, parent, rectangle, owner);
    }


    /**
     * Creates a new button on the given card with the provided geometry.
     *
     *
     * @param context The execution context.
     * @param parent The card that this button will belong to.
     * @param geometry The bounding rectangle of the new button.
     * @return The new button.
     */
    public static ButtonPart fromGeometry(ExecutionContext context, CardPart parent, Rectangle geometry, Owner owner) {
        ButtonPart button = new ButtonPart(ButtonStyle.ROUND_RECT, parent, owner);
        button.initProperties(context, geometry, parent.getPartModel());
        return button;
    }

    /**
     * Creates a new button view from an existing button data model.
     *
     *
     * @param context The execution context.
     * @param parent The card that this button will belong to.
     * @param partModel The data model representing this button.
     * @return The new button.
     * @throws Exception Thrown if an error occurs instantiating the button.
     */
    public static ButtonPart fromModel(ExecutionContext context, CardPart parent, ButtonModel partModel) throws HtException {
        ButtonStyle style = ButtonStyle.fromName(partModel.getKnownProperty(context, ButtonModel.PROP_STYLE).stringValue());
        ButtonPart button = new ButtonPart(style, parent, partModel.getOwner());

        button.partModel = partModel;
        return button;
    }

    @Override
    @RunOnDispatch
    public void partOpened(ExecutionContext context) {
        super.partOpened(context);
        partModel.addPropertyChangedObserver(this);
    }

    @Override
    @RunOnDispatch
    public void partClosed(ExecutionContext context) {
        super.partClosed(context);
        partModel.removePropertyChangedObserver(this);
        PeriodicMessageManager.getInstance().removeWithin(getPartModel());
    }

    @Override
    public CardPart getCard() {
        return parent.get();
    }

    @Override
    public CardLayerPart getPart() {
        return this;
    }

    @Override
    public ToolType getEditTool() {
        return ToolType.BUTTON;
    }

    @Override
    @RunOnDispatch
    public void replaceViewComponent(ExecutionContext context, Component oldButtonComponent, Component newButtonComponent) {
        CardPart cardPart = parent.get();
        if (cardPart != null) {
            cardPart.replaceViewComponent(context, this, oldButtonComponent, newButtonComponent);
        }
    }

    @Override
    public PartType getType() {
        return PartType.BUTTON;
    }

    @Override
    public JComponent getComponent() {
        return this.getButtonComponent();
    }

    @Override
    public PartModel getPartModel() {
        return partModel;
    }

    @Override
    @RunOnDispatch
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (SwingUtilities.isLeftMouseButton(e) && !isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_DOWN.messageName);
            MouseStillDown.then(() -> getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_STILL_DOWN.messageName));
        }
    }

    @Override
    @RunOnDispatch
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        boolean isStillInFocus = new Rectangle(this.getButtonComponent().getSize()).contains(e.getPoint());

        // Do not set mouseUp if cursor is not released while over the part
        if (SwingUtilities.isLeftMouseButton(e) && isStillInFocus && !isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_UP.messageName);
        }
    }

    @Override
    @RunOnDispatch
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);

        if (!isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_ENTER.messageName);
            PeriodicMessageManager.getInstance().addWithin(getPartModel());
        }
    }

    @Override
    @RunOnDispatch
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);

        if (!isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_LEAVE.messageName);
            PeriodicMessageManager.getInstance().removeWithin(getPartModel());
        }
    }

    @Override
    @RunOnDispatch
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (e.getClickCount() == 2 && !isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_DOUBLE_CLICK.messageName);
        }
    }

    @Override
    @RunOnDispatch
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_STYLE:
                setStyle(context, ButtonStyle.fromName(newValue.stringValue()));
                break;
            case ButtonModel.PROP_TOP:
            case ButtonModel.PROP_LEFT:
            case ButtonModel.PROP_WIDTH:
            case ButtonModel.PROP_HEIGHT:
                getButtonComponent().setBounds(partModel.getRect(context));
                getButtonComponent().validate();
                getButtonComponent().repaint();
                break;
            case ButtonModel.PROP_ENABLED:
                setEnabledOnCard(context, newValue.booleanValue());
                break;
            case ButtonModel.PROP_VISIBLE:
                setVisibleWhenBrowsing(context, newValue.booleanValue());
                break;
            case ButtonModel.PROP_ZORDER:
                getCard().onDisplayOrderChanged(context);
                break;
        }
    }

    private void initProperties(ExecutionContext context, Rectangle geometry, PartModel parentPartModel) {
        CardPart cardPart = parent.get();
        if (cardPart != null) {
            int id = cardPart.getCardModel().getStackModel().getNextButtonId();
            partModel = ButtonModel.newButtonModel(context, id, geometry, owner, parentPartModel);
        }
    }
}
