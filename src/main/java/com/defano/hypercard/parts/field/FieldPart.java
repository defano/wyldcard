package com.defano.hypercard.parts.field;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.awt.MouseStillDown;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.parts.DeferredKeyEventComponent;
import com.defano.hypercard.parts.card.CardLayerPart;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.model.PropertiesModel;
import com.defano.hypercard.parts.model.PropertyChangeObserver;
import com.defano.hypercard.runtime.HyperCardProperties;
import com.defano.hypercard.runtime.PeriodicMessageManager;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.context.FontContext;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.runtime.interpreter.Interpreter;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.utils.Range;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The controller object associated with a field on the card.
 *
 * See {@link FieldModel} for the model object associated with this controller.
 * See {@link StyleableField} for the view object associated with this view.
 */
public class FieldPart extends StyleableField implements CardLayerPart, Searchable, PropertyChangeObserver, DeferredKeyEventComponent {

    private static final int DEFAULT_WIDTH = 250;
    private static final int DEFAULT_HEIGHT = 100;

    private final Owner owner;
    private FieldModel partModel;
    private final WeakReference<CardPart> parent;
    private AtomicBoolean redispatchInProgress = new AtomicBoolean(false);

    private FieldPart(FieldStyle style, CardPart parent, Owner owner) {
        super(style);

        this.owner = owner;
        this.parent = new WeakReference<>(parent);
    }

    /**
     * Creates a new field with default attributes on the given card.
     * @param parent The card in which the field should be generated.
     * @return The newly created FieldPart
     */
    public static FieldPart newField(CardPart parent, Owner owner) {
        return newField(parent, owner, new Rectangle(parent.getWidth() / 2 - (DEFAULT_WIDTH / 2), parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2), DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    /**
     * Creates a new field with default attributes on the given card.
     * @param parent The card in which the field should be generated.
     * @return The newly created FieldPart
     */
    public static FieldPart newField(CardPart parent, Owner owner, Rectangle rectangle) {
        FieldPart newField = new FieldPart(FieldStyle.TRANSPARENT, parent, owner);

        // Place the field in the center of the card
        newField.initProperties(rectangle, parent.getPartModel());
        newField.partModel.setKnownProperty(FieldModel.PROP_TEXTFONT, new Value(FontContext.getInstance().getFocusedTextStyle().getFontFamily()));
        newField.partModel.setKnownProperty(FieldModel.PROP_TEXTSIZE, new Value(FontContext.getInstance().getFocusedTextStyle().getFontSize()));
        newField.partModel.setKnownProperty(FieldModel.PROP_TEXTSTYLE, FontContext.getInstance().getFocusedTextStyle().getHyperTalkStyle());

        return newField;
    }

    /**
     * Creates a new field from an existing field data model.
     *
     * @param parent The card in which the field should be created.
     * @param model The data model of the field to be created.
     * @return The newly created field.
     */
    public static FieldPart fromModel(CardPart parent, FieldModel model) {
        FieldPart field = new FieldPart(FieldStyle.fromName(model.getKnownProperty(FieldModel.PROP_STYLE).stringValue()), parent, model.getOwner());

        model.setCurrentCardId(parent.getId());
        field.partModel = model;

        return field;
    }

    /** {@inheritDoc} */
    @Override
    public void clearSearchHilights() {
        getHyperCardTextPane().clearSearchHilights();
    }

    /** {@inheritDoc} */
    @Override
    public void applySearchHilight(Range range) {
        getHyperCardTextPane().applySearchHilight(range);
    }

    @Override
    public String getText() {
        return ((FieldModel) getPartModel()).getText();
    }

    /** {@inheritDoc} */
    @Override
    public void partClosed() {
        super.partClosed();

        partModel.removePropertyChangedObserver(this);
        PeriodicMessageManager.getInstance().removeWithin(getPartModel());
    }

    /** {@inheritDoc} */
    @Override
    public void partOpened() {
        super.partOpened();
        partModel.addPropertyChangedObserver(this);
    }

    /** {@inheritDoc} */
    @Override
    public ToolType getEditTool() {
        return ToolType.FIELD;
    }

    /** {@inheritDoc} */
    @Override
    public void replaceViewComponent(Component oldComponent, Component newComponent) {
        CardPart part = parent.get();
        if (part != null) {
            part.replaceViewComponent(this, oldComponent, newComponent);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PartType getType() {
        return PartType.FIELD;
    }

    /** {@inheritDoc} */
    @Override
    public JComponent getComponent() {
        return this.getFieldComponent();
    }

    /** {@inheritDoc} */
    @Override
    public CardLayerPart getPart() {
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public PartModel getPartModel() {
        return partModel;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPartToolActive() {
        return ToolsContext.getInstance().getToolMode() == ToolMode.FIELD;
    }

    /** {@inheritDoc} */
    @Override
    public CardPart getCard() {
        return parent.get();
    }

    /** {@inheritDoc} */
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        // Update the clickText property
        setClickText(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            getPartModel().receiveMessage(SystemMessage.MOUSE_DOWN.messageName);
            MouseStillDown.then(() -> getPartModel().receiveMessage(SystemMessage.MOUSE_STILL_DOWN.messageName));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        boolean isStillInFocus = new Rectangle(this.getFieldComponent().getSize()).contains(e.getPoint());

        // Do not set mouseUp if cursor is not released while over the part
        if (SwingUtilities.isLeftMouseButton(e) && isStillInFocus) {
            getPartModel().receiveMessage(SystemMessage.MOUSE_UP.messageName);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        getPartModel().receiveMessage(SystemMessage.MOUSE_ENTER.messageName);
        PeriodicMessageManager.getInstance().addWithin(getPartModel());
    }

    /** {@inheritDoc} */
    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        getPartModel().receiveMessage(SystemMessage.MOUSE_LEAVE.messageName);
        PeriodicMessageManager.getInstance().removeWithin(getPartModel());
    }

    /** {@inheritDoc} */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        if (e.getClickCount() == 2) {
            getPartModel().receiveMessage(SystemMessage.MOUSE_DOUBLE_CLICK.messageName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);

        if (getHyperCardTextPane().hasFocus() && !redispatchInProgress.get()) {
            getPartModel().receiveAndDeferKeyEvent(SystemMessage.KEY_DOWN.messageName, new ExpressionList(null, String.valueOf(e.getKeyChar())), e, this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        if (getHyperCardTextPane().hasFocus() && !redispatchInProgress.get()) {
            BoundSystemMessage bsm = SystemMessage.fromKeyEvent(e, true);
            if (bsm != null) {
                getPartModel().receiveAndDeferKeyEvent(bsm.message.messageName, bsm.boundArguments, e, this);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onPropertyChanged(PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case FieldModel.PROP_STYLE:
                setStyle(FieldStyle.fromName(newValue.stringValue()));
                break;
            case FieldModel.PROP_SCRIPT:
                try {
                    Interpreter.blockingCompileScript(newValue.stringValue());
                } catch (HtException e) {
                    HyperCard.getInstance().showErrorDialog(e);
                }
                break;
            case FieldModel.PROP_TOP:
            case FieldModel.PROP_LEFT:
            case FieldModel.PROP_WIDTH:
            case FieldModel.PROP_HEIGHT:
                getComponent().setBounds(partModel.getRect());
                getComponent().validate();
                getComponent().repaint();
                break;
            case FieldModel.PROP_VISIBLE:
                setVisibleWhenBrowsing(newValue.booleanValue());
                break;
            case CardLayerPartModel.PROP_ZORDER:
                getCard().onDisplayOrderChanged();
                break;
        }
    }

    private void setClickText(MouseEvent evt) {
        try {
            int clickIndex = getHyperCardTextPane().viewToModel(evt.getPoint());
            int startWordIndex = Utilities.getWordStart(getHyperCardTextPane(), clickIndex);
            int endWordIndex = Utilities.getWordEnd(getHyperCardTextPane(), clickIndex);

            String clickText = getHyperCardTextPane().getStyledDocument().getText(startWordIndex, endWordIndex - startWordIndex);
            ExecutionContext.getContext().getGlobalProperties().defineProperty(HyperCardProperties.PROP_CLICKTEXT, new Value(clickText), true);

        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    private void initProperties(Rectangle geometry, PartModel parentPartModel) {
        CardPart cardPart = parent.get();

        if (cardPart != null) {
            int id = cardPart.getCardModel().getStackModel().getNextFieldId();
            partModel = FieldModel.newFieldModel(id, geometry, owner, parentPartModel);
            partModel.addPropertyChangedObserver(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPendingRedispatch(boolean redispatchInProcess) {
        this.redispatchInProgress.set(redispatchInProcess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchEvent(AWTEvent event) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> getHyperCardTextPane().dispatchEvent(event));
    }
}
