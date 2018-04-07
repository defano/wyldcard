package com.defano.wyldcard.parts.field;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.awt.MouseStillDown;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.DeferredKeyEventComponent;
import com.defano.wyldcard.parts.card.CardLayerPart;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.runtime.HyperCardProperties;
import com.defano.wyldcard.runtime.PeriodicMessageManager;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.FontContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.runtime.interpreter.Interpreter;
import com.defano.wyldcard.util.ThreadUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
public class FieldPart extends StyleableField implements CardLayerPart, Searchable, PropertyChangeObserver, DeferredKeyEventComponent, FocusListener {

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
     *
     * @param context
     * @param parent The card in which the field should be generated.
     * @return The newly created FieldPart
     */
    public static FieldPart newField(ExecutionContext context, CardPart parent, Owner owner) {
        return newField(context, parent, owner, new Rectangle(parent.getWidth() / 2 - (DEFAULT_WIDTH / 2), parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2), DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    /**
     * Creates a new field with default attributes on the given card.
     *
     * @param context
     * @param parent The card in which the field should be generated.
     * @return The newly created FieldPart
     */
    public static FieldPart newField(ExecutionContext context, CardPart parent, Owner owner, Rectangle rectangle) {
        FieldPart newField = new FieldPart(FieldStyle.TRANSPARENT, parent, owner);

        // Place the field in the center of the card
        newField.initProperties(context, rectangle, parent.getPartModel());
        newField.partModel.setKnownProperty(context, FieldModel.PROP_TEXTFONT, new Value(FontContext.getInstance().getFocusedTextStyle().getFontFamily()));
        newField.partModel.setKnownProperty(context, FieldModel.PROP_TEXTSIZE, new Value(FontContext.getInstance().getFocusedTextStyle().getFontSize()));
        newField.partModel.setKnownProperty(context, FieldModel.PROP_TEXTSTYLE, FontContext.getInstance().getFocusedTextStyle().getHyperTalkStyle());

        return newField;
    }

    /**
     * Creates a new field from an existing field data model.
     *
     *
     * @param context
     * @param parent The card in which the field should be created.
     * @param model The data model of the field to be created.
     * @return The newly created field.
     */
    public static FieldPart fromModel(ExecutionContext context, CardPart parent, FieldModel model) {
        FieldPart field = new FieldPart(FieldStyle.fromName(model.getKnownProperty(context, FieldModel.PROP_STYLE).stringValue()), parent, model.getOwner());

        model.setCurrentCardId(parent.getId(context));
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
    public String getText(ExecutionContext context) {
        return ((FieldModel) getPartModel()).getText(context);
    }

    /** {@inheritDoc}
     * @param context*/
    @Override
    public void partClosed(ExecutionContext context) {
        super.partClosed(context);

        partModel.removePropertyChangedObserver(this);
        getHyperCardTextPane().removeFocusListener(this);

        PeriodicMessageManager.getInstance().removeWithin(getPartModel());
    }

    /** {@inheritDoc}
     * @param context*/
    @Override
    public void partOpened(ExecutionContext context) {
        super.partOpened(context);
        partModel.addPropertyChangedObserver(this);
        getHyperCardTextPane().addFocusListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public ToolType getEditTool() {
        return ToolType.FIELD;
    }

    /** {@inheritDoc} */
    @Override
    public void replaceViewComponent(ExecutionContext context, Component oldComponent, Component newComponent) {
        CardPart part = parent.get();
        if (part != null) {
            part.replaceViewComponent(context, this, oldComponent, newComponent);
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

        if (SwingUtilities.isLeftMouseButton(e) && !isPartToolActive()) {
            // Update the clickText property
            setClickText(e);

            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_DOWN.messageName);
            MouseStillDown.then(() -> getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_STILL_DOWN.messageName));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        boolean isStillInFocus = new Rectangle(this.getFieldComponent().getSize()).contains(e.getPoint());

        // Do not set mouseUp if cursor is not released while over the part
        if (SwingUtilities.isLeftMouseButton(e) && isStillInFocus && !isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_UP.messageName);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);

        if (!isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_ENTER.messageName);
            PeriodicMessageManager.getInstance().addWithin(getPartModel());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);

        if (!isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_LEAVE.messageName);
            PeriodicMessageManager.getInstance().removeWithin(getPartModel());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        if (e.getClickCount() == 2 && !isPartToolActive()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.MOUSE_DOUBLE_CLICK.messageName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);

        if (getHyperCardTextPane().hasFocus() && !redispatchInProgress.get() && !isPartToolActive()) {
            getPartModel().receiveAndDeferKeyEvent(new ExecutionContext(), SystemMessage.KEY_DOWN.messageName, new ListExp(null, new LiteralExp(null, String.valueOf(e.getKeyChar()))), e, this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        if (getHyperCardTextPane().hasFocus() && !redispatchInProgress.get() && !isPartToolActive()) {
            BoundSystemMessage bsm = SystemMessage.fromKeyEvent(e, true);
            if (bsm != null) {
                getPartModel().receiveAndDeferKeyEvent(new ExecutionContext(), bsm.message.messageName, bsm.boundArguments, e, this);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case FieldModel.PROP_STYLE:
                setStyle(context, FieldStyle.fromName(newValue.stringValue()));
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
                getCard().onDisplayOrderChanged(context);
                break;
        }
    }

    private void setClickText(MouseEvent evt) {
        try {
            int clickIndex = getHyperCardTextPane().viewToModel(evt.getPoint());
            int startWordIndex = Utilities.getWordStart(getHyperCardTextPane(), clickIndex);
            int endWordIndex = Utilities.getWordEnd(getHyperCardTextPane(), clickIndex);

            String clickText = getHyperCardTextPane().getStyledDocument().getText(startWordIndex, endWordIndex - startWordIndex);
            HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_CLICKTEXT, new Value(clickText), true);

        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    private void initProperties(ExecutionContext context, Rectangle geometry, PartModel parentPartModel) {
        CardPart cardPart = parent.get();

        if (cardPart != null) {
            int id = cardPart.getCardModel().getStackModel().getNextFieldId();
            partModel = FieldModel.newFieldModel(context, id, geometry, owner, parentPartModel);
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

    @Override
    public void focusGained(FocusEvent e) {
        if (getHyperCardTextPane().isEditable()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.OPEN_FIELD.messageName);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (getHyperCardTextPane().isEditable()) {
            getPartModel().receiveMessage(new ExecutionContext(), SystemMessage.EXIT_FIELD.messageName);
        }
    }
}
