package com.defano.wyldcard.parts.card;

import com.defano.jmonet.canvas.layer.ImageLayerSet;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.awt.MouseListenable;
import com.defano.wyldcard.awt.MouseStillDown;
import com.defano.wyldcard.parts.Part;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.clipboard.CardPartTransferHandler;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.field.FieldPart;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.PartsTable;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.DefaultPartToolManager;
import com.defano.wyldcard.runtime.serializer.Serializer;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.exception.HtException;
import com.defano.jmonet.canvas.JMonetCanvas;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.canvas.observable.CanvasCommitObserver;
import com.defano.jmonet.clipboard.CanvasTransferDelegate;
import com.defano.jmonet.clipboard.CanvasTransferHandler;
import com.defano.jmonet.tools.SelectionTool;
import com.defano.jmonet.tools.base.AbstractSelectionTool;
import com.defano.jmonet.tools.builder.PaintTool;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The "controller" object representing a card in the stack. Note that a card cannot exist apart from a Stack; this
 * limitation is imposed by the fact that a CardPart represents the merged view of the card and background (graphics,
 * buttons, and fields).
 *
 * See {@link CardLayeredPane} for the view object.
 * See {@link CardModel} for the model object.
 */
public class CardPart extends CardLayeredPane implements Part, CanvasCommitObserver, CanvasTransferDelegate, MouseListenable, KeyListener, PropertyChangeObserver {

    private CardModel cardModel;

    private final static int CANVAS_UNDO_DEPTH = 20;

    private final PartsTable<FieldPart> fields = new PartsTable<>();
    private final PartsTable<ButtonPart> buttons = new PartsTable<>();

    private final EditingBackgroundObserver editingBackgroundObserver = new EditingBackgroundObserver();
    private final ForegroundScaleObserver foregroundScaleObserver = new ForegroundScaleObserver();
    private final BackgroundScaleObserver backgroundScaleObserver = new BackgroundScaleObserver();
    private final CardModelObserver cardModelObserver = new CardPartModelObserver();

    private Disposable editingBackgroundSubscription;
    private Disposable foregroundScaleSubscription;
    private Disposable backgroundScaleSubscription;

    /**
     * Instantiates the CardPart occurring at a specified position in a the stack.
     *
     *
     * @param context The execution context.
     * @param cardIndex The location in the stack where the card should be created.
     * @param stack The stack data model containing the card to return
     * @return The CardPart.
     * @throws HtException Thrown if an error occurs creating the card.
     */
    public static CardPart fromPositionInStack(ExecutionContext context, int cardIndex, StackModel stack) throws HtException {
        return fromModel(stack.getCardModel(cardIndex), context);
    }

    /**
     * Instantiates a CardPart given a {@link CardModel} and {@link StackModel}.
     *
     * @param model The model of the card to instantiate.
     * @param context The execution context.
     * @return The fully instantiated CardPart.
     * @throws HtException Thrown if an error occurs instantiating the card.
     */
    public static CardPart fromModel(CardModel model, ExecutionContext context) throws HtException {
        return skeletonFromModel(context, model);
    }

    /**
     * Produces a skeleton CardPart object intended only for programmatic interaction with the card (as used for card
     * sort operations). Skeleton CardPart objects cannot correctly be displayed onscreen or interacted with. Creating
     * a skeleton CardPart is much faster than creating a full CardPart.
     *
     * Object returned does not contain a built graphics canvas; mouse and keyboard listeners are not registered; and
     * part components (button and field views) are not updated to reflect the values in their model.
     *
     * TODO: This method indicates a broken model-view-controller pattern and should be eliminated.
     *
     * @param context The execution context.
     * @param model The model of the card to instantiate.
     * @return A partially constructed CardPart useful for programmatic inspection
     * @throws HtException Thrown if an error occurs constructing the CardPart.
     */
    @RunOnDispatch
    public static CardPart skeletonFromModel(ExecutionContext context, CardModel model) throws HtException {
        CardPart card = new CardPart();
        card.cardModel = model;

        // Add card parts to this card
        for (PartModel thisPart : model.getPartModels(context)) {
            card.addViewFromModel(context, thisPart);
        }

        // Add background parts to this card
        for (PartModel thisPart : model.getBackgroundModel().getPartModels(context)) {
            card.addViewFromModel(context, thisPart);
        }

        return card;
    }

    /**
     * Imports an existing part (button or field) into this card.
     *
     * Note that this differs from {@link #addField(ExecutionContext, FieldPart)} or {@link #addButton(ExecutionContext, ButtonPart)}
     * in that a new ID for the part is generated before it is added to the card. This method is typically used to
     * "paste" a copied part from another card onto this card.
     *
     *
     * @param context The execution context.
     * @param part The part to be imported.
     * @return The newly imported part (identical to the given part, but with a new ID)
     * @throws HtException Thrown if an error occurs importing the part.
     */
    @RunOnDispatch
    public Part importPart(ExecutionContext context, Part part, CardLayer layer) throws HtException {
        if (part instanceof ButtonPart) {
            return importButton(context, (ButtonPart) part, layer);
        } else if (part instanceof FieldPart) {
            return importField(context, (FieldPart) part, layer);
        }

        throw new IllegalArgumentException("Bug! Unimplemented import of part type: " + part.getClass());
    }

    /**
     * Adds a new button (with default attributes) to this card. Represents the behavior of the user choosing
     * "New Button" from the Objects menu.
     * // TODO: Should probably be moved to CardModel
     * @param context The execution context.
     */
    @RunOnDispatch
    public void newButton(ExecutionContext context) {
        CardLayer layer = CardLayerPart.getActivePartLayer();
        ButtonPart newButton = ButtonPart.newButton(context, this, layer.asOwner());
        addButton(context, newButton);
        newButton.getPartModel().receiveMessage(context.bind(this), SystemMessage.NEW_BUTTON.messageName);

        // When a new button is created, make the button tool active and select the newly created button
        WyldCard.getInstance().getToolsManager().forceToolSelection(ToolType.BUTTON, false);
        WyldCard.getInstance().getPartToolManager().setSelectedPart(newButton);
    }

    /**
     * Adds a new button (with default attributes) to this card using the given bounding rectangle. Represents the
     * behavior of the user command-draging with the button tool active.
     * // TODO: Should probably be moved to CardModel
     */
    @RunOnDispatch
    public ButtonPart newButton(ExecutionContext context, Rectangle rectangle) {
        CardLayer layer = CardLayerPart.getActivePartLayer();
        ButtonPart newButton = ButtonPart.newButton(context, this, layer.asOwner(), rectangle);
        addButton(context, newButton);
        newButton.getPartModel().receiveMessage(context.bind(this), SystemMessage.NEW_BUTTON.messageName);

        return newButton;
    }

    /**
     * Adds a new field (with default attributes) to this card. Represents the behavior of the user choosing
     * "New Field" from the Objects menu.
     * // TODO: Should probably be moved to CardModel
     * @param context The execution context.
     */
    @RunOnDispatch
    public void newField(ExecutionContext context) {
        CardLayer layer = CardLayerPart.getActivePartLayer();
        FieldPart newField = FieldPart.newField(new ExecutionContext(), this, layer.asOwner());
        addField(context, newField);
        newField.getPartModel().receiveMessage(context.bind(this), SystemMessage.NEW_FIELD.messageName);

        // When a new button is created, make the button tool active and select the newly created button
        WyldCard.getInstance().getToolsManager().forceToolSelection(ToolType.FIELD, false);
        WyldCard.getInstance().getPartToolManager().setSelectedPart(newField);
    }

    /**
     * Adds a new field with a given geometry to this card.
     * // TODO: Should probably be moved to CardModel
     */
    @RunOnDispatch
    public FieldPart newField(ExecutionContext context, Rectangle rectangle) {
        CardLayer layer = CardLayerPart.getActivePartLayer();
        FieldPart newField = FieldPart.newField(context, this, layer.asOwner(), rectangle);
        addField(context, newField);
        newField.getPartModel().receiveMessage(context.bind(this), SystemMessage.NEW_FIELD.messageName);
        return newField;
    }

    /**
     * Gets an unordered collection of buttons that exist on this card in the active layer.
     * @return The buttons on this card.
     */
    public Collection<ButtonPart> getButtons() {
        return buttons.getParts();
    }

    /**
     * Gets an unordered collection of fields that exist on this card in the active layer.
     * @return The fields on this card.
     */
    public Collection<FieldPart> getFields() {
        return fields.getParts();
    }

    /**
     * Gets an unordered collection of card parts (buttons and fields) that exist on this card.
     * @return The collection of existent buttons and fields.
     */
    public Collection<CardLayerPart> getCardParts() {
        ArrayList<CardLayerPart> parts = new ArrayList<>();
        parts.addAll(getButtons());
        parts.addAll(getFields());
        return parts;
    }

    /**
     * Gets the data model associated with this card.
     * @return The CardModel for this card.
     */
    public CardModel getCardModel() {
        return cardModel;
    }

    /**
     * Gets the active paint canvas associated with this card, either the foreground or background canvas depending
     * on whether the user is currently editing the card's background.
     * @return The active paint canvas for this card.
     */
    public JMonetCanvas getCanvas() {
        return WyldCard.getInstance().getToolsManager().isEditingBackground() ? getBackgroundCanvas() : getForegroundCanvas();
    }

    /**
     * Hides or shows the card foreground, including the canvas and all parts. When made visible, only those parts
     * whose visible property is true will actually become visible.
     *
     * @param context The execution context.
     * @param visible Shows the foreground when true; hides it otherwise.
     */
    @RunOnDispatch
    private void setForegroundVisible(ExecutionContext context, boolean visible) {
        if (getForegroundCanvas() != null) {
            getForegroundCanvasScrollPane().setVisible(visible);

            setPartsOnLayerVisible(context, Owner.CARD, visible);
        }
    }

    /**
     * Hides or shows all parts on the card's foreground. When made visible, only those parts whose visible property is
     * true will actually become visible.
     *
     * @param context The execution context.
     * @param visible True to show card parts; false to hide them
     */
    @RunOnDispatch
    private void setPartsOnLayerVisible(ExecutionContext context, Owner owningLayer, boolean visible) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (PartModel thisPartModel : getCardModel().getPartModels(context)) {
                if (thisPartModel.getOwner() == owningLayer) {
                    if (!visible) {
                        getPart(context, thisPartModel).getComponent().setVisible(false);
                    } else {
                        getPart(context, thisPartModel).getComponent().setVisible(thisPartModel.getKnownProperty(context, PartModel.PROP_VISIBLE).booleanValue());
                    }
                }
            }
        });
    }

    public boolean isForegroundHidden() {
        return getForegroundCanvas() == null || !getForegroundCanvas().isVisible();
    }

    /**
     * Hides or shows the background layer for this card.
     * @param context The execution context.
     * @param isVisible True to show the background; false to hide it.
     */
    @RunOnDispatch
    private void setBackgroundVisible(ExecutionContext context, boolean isVisible) {
        if (getBackgroundCanvas() != null) {
            getBackgroundCanvas().setVisible(isVisible);
        }

        setPartsOnLayerVisible(context, Owner.BACKGROUND, isVisible);
    }

    /**
     * Removes a part (button or field) from this card. Has no effect if the part is not on this card.
     * @param context The execution context.
     * @param part The part to be removed.
     */
    @RunOnDispatch
    private void removePart(ExecutionContext context, PartModel part) {
        if (part instanceof ButtonModel) {
            removeButtonView(context, (ButtonModel) part);
        } else if (part instanceof FieldModel) {
            removeFieldView(context, (FieldModel) part);
        } else {
            throw new IllegalArgumentException("Bug! Unimplemented remove of part type: " + part.getClass());
        }
    }

    /**
     * Removes a button from this card view. Does not affect the {@link CardModel}. Has no effect if the button does
     * not exist on the card.
     *
     * @param context The execution context.
     * @param buttonModel The button to be removed.
     */
    @RunOnDispatch
    private void removeButtonView(ExecutionContext context, ButtonModel buttonModel) {
        ButtonPart button = buttons.getPart(context, buttonModel);

        if (button != null) {
            buttons.removePart(context, button);
            removeSwingComponent(button.getComponent());
            button.partClosed(context);
        }
    }

    /**
     * Removes a field from this card view. Does not affect the {@link CardModel}. Has no effect if the field does not
     * exist on the card.
     *
     * @param context The execution context.
     * @param fieldModel The field to be removed.
     */
    @RunOnDispatch
    private void removeFieldView(ExecutionContext context, FieldModel fieldModel) {
        FieldPart field = fields.getPart(context, fieldModel);

        if (field != null) {
            fields.removePart(context, field);
            removeSwingComponent(field.getComponent());
            field.partClosed(context);
        }
    }

    /**
     * Swap the Swing component associated with a given part (button or field) for a new component. This is useful
     * when changing button or field styles.
     *
     * @param context The execution context.
     * @param forPart The part whose Swing component should be replaced.
     * @param oldButtonComponent The old Swing component associated with this part.
     * @param newButtonComponent The new Swing component to be used.
     */
    @RunOnDispatch
    public void replaceViewComponent(ExecutionContext context, Part forPart, Component oldButtonComponent, Component newButtonComponent) {
        CardLayer partLayer = getCardLayer(oldButtonComponent);
        removeSwingComponent(oldButtonComponent);
        addSwingComponent(newButtonComponent, forPart.getRect(context), partLayer);
        forPart.partOpened(context);
        onDisplayOrderChanged(context);
    }

    /**
     * Indicates that the z-order of a part changed (and that components should be reordered on the pane according to
     * their new position).
     * @param context The execution context.
     */
    public void onDisplayOrderChanged(ExecutionContext context) {
        SwingUtilities.invokeLater(() -> {
            for (PartModel thisPart : getCardModel().getPartsInDisplayOrder(context)) {
                moveToFront(getPart(context, thisPart).getComponent());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCommit(PaintCanvas canvas, ImageLayerSet imageLayerSet, BufferedImage canvasImage) {
        if (WyldCard.getInstance().getToolsManager().isEditingBackground()) {
            cardModel.getBackgroundModel().setBackgroundImage(canvasImage);
        } else {
            cardModel.setCardImage(canvasImage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public BufferedImage copySelection() {
        PaintTool activeTool = WyldCard.getInstance().getToolsManager().getPaintTool();
        if (activeTool instanceof AbstractSelectionTool) {
            return ((AbstractSelectionTool) activeTool).getSelectedImage();
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void deleteSelection() {
        PaintTool activeTool = WyldCard.getInstance().getToolsManager().getPaintTool();
        if (activeTool instanceof AbstractSelectionTool) {
            ((AbstractSelectionTool) activeTool).deleteSelection();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void pasteSelection(BufferedImage image) {
        int cardCenterX = getWidth() / 2;
        int cardCenterY = getHeight() / 2;

        SelectionTool tool = (SelectionTool) WyldCard.getInstance().getToolsManager().forceToolSelection(ToolType.SELECT, false);
        tool.createSelection(image, new Point(cardCenterX - image.getWidth() / 2, cardCenterY - image.getHeight() / 2));
    }

    /**
     * Gets a screenshot of this card; a pixel accurate rendering of the card as would be visible to the user when the
     * card is visible in the stack window including graphics and part layers (the rendering of which will differ
     * based on Swing's current look-and-feel setting).
     *
     * @return A screenshot of this card.
     */
    public BufferedImage getScreenshot() {

        // Swing cannot print components that are not actively displayed in a window (this is a side effect of the
        // native component peering architecture). Therefore, if this card is not already being displayed in a
        // window, we will need to create one and place ourselves inside of it before attempting to print. However,
        // note that a card (or any component) cannot be the content pane of multiple frames simultaneously, so we
        // utilize the screenshot buffer frame only when not already attached to a card window.
        if (getRootPane() == null) {
            WyldCard.getInstance().getWindowManager().getScreenshotBufferWindow().setContentPane(this);
        }

        BufferedImage screenshot = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = screenshot.createGraphics();
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            CardPart.this.printAll(g);
        });

        g.dispose();
        return screenshot;
    }

    /** {@inheritDoc} */
    @Override
    public int getHeight() {
        return cardModel.getStackModel().getHeight(new ExecutionContext());
    }

    /** {@inheritDoc} */
    @Override
    public int getWidth() {
        return cardModel.getStackModel().getWidth(new ExecutionContext());
    }

    /**
     * Notify all parts in this container that they are closing (ostensibly because the container itself is closing).
     * @param context The execution context.
     */
    @RunOnDispatch
    private void notifyPartsClosing(ExecutionContext context) {
        for (ButtonPart p : buttons.getParts()) {
            p.partClosed(context);
        }

        for (FieldPart p : fields.getParts()) {
            p.partClosed(context);
        }
    }

    /**
     * Imports an existing button into this card. Note that this differs from {@link #addButton(ExecutionContext, ButtonPart)} in that a
     * new ID for the part is generated before it is added to the card. This method is typically used to "paste" a
     * copied button from another card onto this card.
     *
     *
     * @param context The execution context.
     * @param part The button to be imported.
     * @param layer The card layer (card or background) on which to import this part
     * @return The newly imported button (identical to the given part, but with a new ID)
     * @throws HtException Thrown if an error occurs importing the part.
     */
    @RunOnDispatch
    private ButtonPart importButton(ExecutionContext context, ButtonPart part, CardLayer layer) throws HtException {
        ButtonModel model = (ButtonModel) Serializer.copy(part.getPartModel());
        model.defineProperty(PartModel.PROP_ID, new Value(cardModel.getStackModel().getNextButtonId()), true);
        model.setOwner(layer.asOwner());

        ButtonPart newButton = ButtonPart.fromModel(context, this, model);
        addButton(context, newButton);
        newButton.getPartModel().receiveMessage(context.bind(this), SystemMessage.NEW_BUTTON.messageName);

        return newButton;
    }

    /**
     * Imports an existing field into this card. Note that this differs from {@link #addField(ExecutionContext, FieldPart)} in that a
     * new ID for the part is generated before it is added to the card. This method is typically used to "paste" a
     * copied field from another card onto this card.
     *
     *
     * @param context The execution context.
     * @param part The field to be imported.
     * @param layer The card layer (card or background) on which to import this part
     * @return The newly imported field (identical to the given part, but with a new ID)
     */
    @RunOnDispatch
    private FieldPart importField(ExecutionContext context, FieldPart part, CardLayer layer) {
        FieldModel model = (FieldModel) Serializer.copy(part.getPartModel());
        model.defineProperty(PartModel.PROP_ID, new Value(cardModel.getStackModel().getNextFieldId()), true);
        model.setOwner(layer.asOwner());

        FieldPart newField = FieldPart.fromModel(context, this, model);
        addField(context, newField);
        newField.getPartModel().receiveMessage(context.bind(this), SystemMessage.NEW_FIELD.messageName);

        return newField;
    }

    /**
     * Adds a field to this card in the layer indicated by the model. Assumes that the field has a unique ID.
     * @param context The execution context.
     * @param field The field to add to this card.
     */
    @RunOnDispatch
    private void addField(ExecutionContext context, FieldPart field) {
        if (field.getPartModel().getLayer() == CardLayer.CARD_PARTS) {
            cardModel.addPartModel(field.getPartModel());
        } else if (field.getPartModel().getLayer() == CardLayer.BACKGROUND_PARTS) {
            cardModel.getBackgroundModel().addFieldModel((FieldModel) field.getPartModel());
        }

        fields.addPart(context, field);
        addSwingComponent(field.getComponent(), field.getRect(context), field.getPartModel().getLayer());
        field.partOpened(context);
    }

    /**
     * Adds a button to this card. Assumes the button has a unique ID.
     * @param context The execution context.
     * @param button The button to be added.
     */
    @RunOnDispatch
    private void addButton(ExecutionContext context, ButtonPart button) {
        if (button.getPartModel().getLayer() == CardLayer.CARD_PARTS) {
            cardModel.addPartModel(button.getPartModel());
        } else if (button.getPartModel().getLayer() == CardLayer.BACKGROUND_PARTS) {
            cardModel.getBackgroundModel().addButtonModel((ButtonModel) button.getPartModel());
        }

        buttons.addPart(context, button);
        addSwingComponent(button.getComponent(), button.getRect(context), button.getPartModel().getLayer());
        button.partOpened(context);
    }

    /**
     * Adds a part view to the layer of this card specified in its model. Does not affect the {@link CardModel}.
     *
     *
     * @param context The execution context.
     * @param thisPart The data model of the part to be added.
     * @throws HtException Thrown if an error occurs adding the part.
     */
    @RunOnDispatch
    private void addViewFromModel(ExecutionContext context, PartModel thisPart) throws HtException {
        switch (thisPart.getType()) {
            case BUTTON:
                ButtonPart button = ButtonPart.fromModel(context, this, (ButtonModel) thisPart);
                buttons.addPart(context, button);
                addSwingComponent(button.getComponent(), button.getRect(context), thisPart.getLayer());
                break;
            case FIELD:
                FieldPart field = FieldPart.fromModel(context, this, (FieldModel) thisPart);
                fields.addPart(context, field);
                addSwingComponent(field.getComponent(), field.getRect(context), thisPart.getLayer());
                break;
        }
    }

    /**
     * Removes a Swing component from this card's JLayeredPane.
     * @param component The component to remove.
     */
    @RunOnDispatch
    private void removeSwingComponent(Component component) {
        remove(component);
        revalidate(); repaint();
    }

    /**
     * Adds a Swing component to this card's JLayeredPane.
     * @param component The component to add.
     * @param bounds The component's desired location and size.
     */
    @RunOnDispatch
    private void addSwingComponent(Component component, Rectangle bounds, CardLayer layer) {
        component.setBounds(bounds);
        addToCardLayer(component, layer);
        moveToFront(component);

        revalidate(); repaint();
    }

    /** {@inheritDoc} */
    @Override
    public PartType getType() {
        return PartType.CARD;
    }

    /** {@inheritDoc} */
    @Override
    public PartModel getPartModel() {
        return getCardModel();
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public void partOpened(ExecutionContext context) {

        StackModel stack = getOwningStackModel();
        Dimension dimension = stack.getSize(context);

        // Setup part cut, copy and paste
        setTransferHandler(new CardPartTransferHandler(this));

        // Setup the foreground paint canvas
        setForegroundCanvas(new JMonetScrollPane(new JMonetCanvas(cardModel.getCardImage(dimension), CANVAS_UNDO_DEPTH)));
        getForegroundCanvas().addCanvasCommitObserver(this);
        getForegroundCanvas().setTransferHandler(new CanvasTransferHandler(getForegroundCanvas(), this));
        getForegroundCanvas().setSize(stack.getWidth(context), stack.getHeight(context));

        // Setup the background paint canvas
        setBackgroundCanvas(new JMonetScrollPane(new JMonetCanvas(getCardModel().getBackgroundModel().getBackgroundImage(dimension), CANVAS_UNDO_DEPTH)));
        getBackgroundCanvas().addCanvasCommitObserver(this);
        getBackgroundCanvas().setTransferHandler(new CanvasTransferHandler(getBackgroundCanvas(), this));
        getBackgroundCanvas().setSize(stack.getWidth(context), stack.getHeight(context));

        // Resize card (Swing) component
        setMaximumSize(stack.getSize(context));
        setSize(stack.getWidth(context), stack.getHeight(context));

        // Fire property change observers on the parts (so that they can draw themselves in their correct initial state)
        for (ButtonPart thisButton : buttons.getParts()) {
            thisButton.getPartModel().notifyPropertyChangedObserver(context, thisButton);
        }

        for (FieldPart thisField : fields.getParts()) {
            thisField.getPartModel().notifyPropertyChangedObserver(context, thisField);
        }

        editingBackgroundSubscription = WyldCard.getInstance().getToolsManager().isEditingBackgroundProvider().subscribe(editingBackgroundObserver);
        foregroundScaleSubscription = getForegroundCanvas().getScaleObservable().subscribe(foregroundScaleObserver);
        backgroundScaleSubscription = getBackgroundCanvas().getScaleObservable().subscribe(backgroundScaleObserver);

        getForegroundCanvas().addMouseListener(this);
        getForegroundCanvas().addKeyListener(this);

        getPartModel().receiveMessage(context.bind(this), SystemMessage.OPEN_CARD.messageName);
        ((CardModel) getPartModel()).setObserver(cardModelObserver);

        getCardModel().addPropertyChangedObserver(this);
        getCardModel().notifyPropertyChangedObserver(context, this);

        getCardModel().getBackgroundModel().addPropertyChangedObserver(this);
        getCardModel().getBackgroundModel().notifyPropertyChangedObserver(context, this);
    }

    /** {@inheritDoc}
     * @param context*/
    @Override
    @RunOnDispatch
    public void partClosed(ExecutionContext context) {
        getPartModel().receiveMessage(context.bind(this), SystemMessage.CLOSE_CARD.messageName);

        // Lets parts know they're about to go away
        notifyPartsClosing(context);

        // Remove their Swing components from the card to free memory
        removeAll();

        editingBackgroundSubscription.dispose();
        foregroundScaleSubscription.dispose();
        backgroundScaleSubscription.dispose();

        getForegroundCanvas().removeMouseListener(this);
        getForegroundCanvas().removeKeyListener(this);

        getForegroundCanvas().dispose();
        getBackgroundCanvas().dispose();

        setTransferHandler(null);
        ((CardModel) getPartModel()).setObserver(null);

        getCardModel().removePropertyChangedObserver(this);
        getCardModel().getBackgroundModel().removePropertyChangedObserver(this);

        super.dispose();
    }

    /**
     * Returns the CardLayerPart represented by the given PartModel.
     *
     *
     * @param context The execution context.
     * @param partModel The PartModel associated with the desired Part.
     * @throws IllegalArgumentException Thrown if no such part exists on this card
     * @return The matching CardLayerPart
     */
    public CardLayerPart getPart(ExecutionContext context, PartModel partModel) {
        CardLayerPart part = null;

        if (partModel instanceof FieldModel) {
            part = fields.getPart(context, partModel);
        } else if (partModel instanceof ButtonModel) {
            part = buttons.getPart(context, partModel);
        }

        if (part == null) {
            throw new IllegalArgumentException("No part on card " + this.getCardModel() + " for model: " + partModel);
        } else {
            return part;
        }
    }

    @Override
    @RunOnDispatch
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_DOUBLE_CLICK.messageName);
        }

        // Search results are reset/cleared whenever the card is clicked
        WyldCard.getInstance().getSearchManager().reset();
    }

    @Override
    @RunOnDispatch
    public void mousePressed(MouseEvent e) {
        getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_DOWN.messageName);
        MouseStillDown.then(() -> getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_STILL_DOWN.messageName));
    }

    @Override
    @RunOnDispatch
    public void mouseReleased(MouseEvent e) {
        getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_UP.messageName);
    }

    @Override
    @RunOnDispatch
    public void mouseEntered(MouseEvent e) {
        getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_ENTER.messageName);
    }

    @Override
    @RunOnDispatch
    public void mouseExited(MouseEvent e) {
        getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.MOUSE_LEAVE.messageName);
    }

    @Override
    @RunOnDispatch
    public void keyTyped(KeyEvent e) {
        getPartModel().receiveMessage(new ExecutionContext(this), SystemMessage.KEY_DOWN.messageName, new ListExp(null, new LiteralExp(null, String.valueOf(e.getKeyChar()))));
    }

    @Override
    @RunOnDispatch
    public void keyPressed(KeyEvent e) {
        BoundSystemMessage bsm = SystemMessage.fromKeyEvent(e, false);
        if (bsm != null) {
            getPartModel().receiveMessage(new ExecutionContext(this), bsm.message.messageName, bsm.boundArguments);
        }
    }

    @Override
    @RunOnDispatch
    public void keyReleased(KeyEvent e) {
        // Nothing to do
    }

    @Override
    @RunOnDispatch
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property.toLowerCase()) {
            case CardModel.PROP_SHOWPICT:
                if (model == getCardModel()) {
                    setCardImageVisible(newValue.booleanValue());
                } else if (model == getCardModel().getBackgroundModel()) {
                    setBackgroundImageVisible(newValue.booleanValue());
                }
                break;
        }
    }

    private class BackgroundScaleObserver implements Consumer<Double> {
        @Override
        public void accept(Double scale) {
            SwingUtilities.invokeLater(() -> {
                setPartsOnLayerVisible(new ExecutionContext(), Owner.BACKGROUND, (scale) == 1.0);
            });
        }
    }

    private class ForegroundScaleObserver implements Consumer<Double> {
        @Override
        public void accept(Double scale) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> {
                setPartsOnLayerVisible(new ExecutionContext(), Owner.CARD, scale == 1.0);
                setPartsOnLayerVisible(new ExecutionContext(), Owner.BACKGROUND, scale == 1.0);
                setBackgroundVisible(new ExecutionContext(), scale == 1.0);
            });
        }
    }

    private class EditingBackgroundObserver implements Consumer<Boolean> {
        @Override
        public void accept(Boolean isEditingBackground) {
            if (getForegroundCanvas() != null && getForegroundCanvas().getScale() != 1.0) {
                getForegroundCanvas().setScale(1.0);
            }

            if (getBackgroundCanvas() != null && getBackgroundCanvas().getScale() != 1.0) {
                getBackgroundCanvas().setScale(1.0);
            }

            ThreadUtils.invokeAndWaitAsNeeded(() -> setForegroundVisible(new ExecutionContext(), !isEditingBackground));
        }
    }

    private class CardPartModelObserver implements CardModelObserver {
        @Override
        public void onPartRemoved(ExecutionContext context, PartModel removedPart) {
            removePart(context, removedPart);
        }
    }
}
