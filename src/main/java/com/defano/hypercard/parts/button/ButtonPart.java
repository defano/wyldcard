package com.defano.hypercard.parts.button;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.model.PropertiesModel;
import com.defano.hypercard.runtime.CompilationUnit;
import com.defano.hypercard.runtime.PeriodicMessageManager;
import com.defano.hypercard.runtime.context.PartToolContext;
import com.defano.hypercard.window.forms.ButtonPropertyEditor;
import com.defano.hypercard.window.forms.ScriptEditor;
import com.defano.hypercard.window.WindowBuilder;
import com.defano.hypercard.parts.card.CardLayerPart;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.model.PropertyChangeObserver;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.*;
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

    private static final int DEFAULT_WIDTH = 160;
    private static final int DEFAULT_HEIGHT = 40;

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
     * @param parent The card that this button will belong to.
     * @return The new button.
     */
    public static ButtonPart newButton(CardPart parent, Owner owner) {
        ButtonPart newButton = fromGeometry(parent, new Rectangle(parent.getWidth() / 2 - (DEFAULT_WIDTH / 2), parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2), DEFAULT_WIDTH, DEFAULT_HEIGHT), owner);

        // When a new button is created, make the button tool active and select the newly created button
        ToolsContext.getInstance().forceToolSelection(ToolType.BUTTON, false);
        PartToolContext.getInstance().setSelectedPart(newButton);

        return newButton;
    }

    /**
     * Creates a new button on the given card with the provided geometry.
     *
     * @param parent The card that this button will belong to.
     * @param geometry The bounding rectangle of the new button.
     * @return The new button.
     */
    public static ButtonPart fromGeometry(CardPart parent, Rectangle geometry, Owner owner) {
        ButtonPart button = new ButtonPart(ButtonStyle.DEFAULT, parent, owner);
        button.initProperties(geometry);
        return button;
    }

    /**
     * Creates a new button view from an existing button data model.
     *
     * @param parent The card that this button will belong to.
     * @param partModel The data model representing this button.
     * @return The new button.
     * @throws Exception Thrown if an error occurs instantiating the button.
     */
    public static ButtonPart fromModel(CardPart parent, ButtonModel partModel) throws HtException {
        ButtonStyle style = ButtonStyle.fromName(partModel.getKnownProperty(ButtonModel.PROP_STYLE).stringValue());
        ButtonPart button = new ButtonPart(style, parent, partModel.getOwner());

        button.partModel = partModel;
        return button;
    }

    @Override
    public void partOpened() {
        super.partOpened();
        partModel.addPropertyChangedObserver(this);
    }

    @Override
    public void partClosed() {
        super.partClosed();
        partModel.removePropertyChangedObserver(this);
        PeriodicMessageManager.getInstance().removeWithin(getPartModel());
    }

    @Override
    public void editScript() {
        WindowBuilder.make(new ScriptEditor())
                .withTitle("Script of button " + partModel.getKnownProperty(ButtonModel.PROP_NAME).stringValue())
                .withModel(partModel)
                .resizeable(true)
                .withLocationStaggeredOver(WindowManager.getStackWindow().getWindowPanel())
                .build();
    }

    @Override
    public void editProperties() {
        WindowBuilder.make(new ButtonPropertyEditor())
                .asModal()
                .resizeable(false)
                .withTitle(getName())
                .withModel(partModel)
                .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                .build();
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
    public void replaceViewComponent(Component oldButtonComponent, Component newButtonComponent) {
        CardPart cardPart = parent.get();
        if (cardPart != null) {
            cardPart.replaceViewComponent(this, oldButtonComponent, newButtonComponent);
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
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            getPartModel().receiveMessage(SystemMessage.MOUSE_DOWN.messageName);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            getPartModel().receiveMessage(SystemMessage.MOUSE_UP.messageName);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        getPartModel().receiveMessage(SystemMessage.MOUSE_ENTER.messageName);
        PeriodicMessageManager.getInstance().addWithin(getPartModel());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        getPartModel().receiveMessage(SystemMessage.MOUSE_LEAVE.messageName);
        PeriodicMessageManager.getInstance().removeWithin(getPartModel());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (e.getClickCount() == 2) {
            getPartModel().receiveMessage(SystemMessage.MOUSE_DOUBLE_CLICK.messageName);
        }
    }

    @Override
    public void onPropertyChanged(PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case ButtonModel.PROP_STYLE:
                setStyle(ButtonStyle.fromName(newValue.stringValue()));
                break;
            case ButtonModel.PROP_SCRIPT:
                try {
                    Interpreter.compileScript(newValue.stringValue());
                } catch (HtException e) {
                    HyperCard.getInstance().showErrorDialog(new HtSemanticException("Didn't understand that.", e));
                }
                break;
            case ButtonModel.PROP_TOP:
            case ButtonModel.PROP_LEFT:
            case ButtonModel.PROP_WIDTH:
            case ButtonModel.PROP_HEIGHT:
                getButtonComponent().setBounds(partModel.getRect());
                getButtonComponent().validate();
                getButtonComponent().repaint();
                break;
            case ButtonModel.PROP_ENABLED:
                setEnabledOnCard(newValue.booleanValue());
                break;
            case ButtonModel.PROP_VISIBLE:
                setVisibleOnCard(newValue.booleanValue());
                break;
            case ButtonModel.PROP_ZORDER:
                getCard().onDisplayOrderChanged();
                break;
        }
    }

    private void initProperties(Rectangle geometry) {
        CardPart cardPart = parent.get();
        if (cardPart != null) {
            int id = cardPart.getStackModel().getNextButtonId();
            partModel = ButtonModel.newButtonModel(id, geometry, owner);
        }
    }
}
