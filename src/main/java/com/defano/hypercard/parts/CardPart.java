/*
 * CardPart
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * CardPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements a card part by extending the Swing panel object.
 */

package com.defano.hypercard.parts;

import com.defano.hypercard.Serializer;
import com.defano.hypercard.context.PartToolContext;
import com.defano.hypercard.context.PartsTable;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.gui.util.FileDrop;
import com.defano.hypercard.gui.util.ImageImporter;
import com.defano.hypercard.parts.clipboard.CardPartTransferHandler;
import com.defano.hypercard.parts.model.*;
import com.defano.hypercard.parts.model.ButtonModel;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.jmonet.canvas.ChangeSet;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.canvas.UndoablePaintCanvas;
import com.defano.jmonet.canvas.observable.CanvasCommitObserver;
import com.defano.jmonet.clipboard.CanvasTransferDelegate;
import com.defano.jmonet.clipboard.CanvasTransferHandler;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.SelectionTool;
import com.defano.jmonet.tools.base.AbstractSelectionTool;
import com.defano.jmonet.tools.base.PaintTool;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * A view object representing a card in the stack; extends the Swing JLayeredPane component.
 */
public class CardPart extends JLayeredPane implements CanvasCommitObserver, CanvasTransferDelegate {

    private final static int BACKGROUND_CANVAS_LAYER = 0;
    private final static int BACKGROUND_PARTS_LAYER = 1;
    private final static int FOREGROUND_CANVAS_LAYER = 2;
    private final static int FOREGROUND_PARTS_LAYER = 3;

    private CardModel cardModel;
    private StackModel stackModel;

    private PartsTable<FieldPart> fields = new PartsTable<>();
    private PartsTable<ButtonPart> buttons = new PartsTable<>();

    private UndoablePaintCanvas foregroundCanvas;
    private UndoablePaintCanvas backgroundCanvas;

    private CardPart() {
        super();
        this.setLayout(null);
    }

    /**
     * Creates a new card in a given stack at a given location from a card data model.
     *
     * @param cardIndex The location in the stack where the card should be added.
     * @param stack The stack in which the card should be added.
     * @return The newly created card.
     * @throws HtException Thrown if an error occurs creating the card.
     */
    public static CardPart fromModel(int cardIndex, StackModel stack) throws HtException {
        CardPart card = new CardPart();
        card.cardModel = stack.getCardModel(cardIndex);
        card.stackModel = stack;

        // Add parts to this card
        for (AbstractPartModel thisPart : card.cardModel.getPartModels()) {
            switch (thisPart.getType()) {
                case BUTTON:
                    ButtonPart button = ButtonPart.fromModel(card, (ButtonModel) thisPart);
                    card.buttons.addPart(button);
                    card.addSwingComponent(button.getComponent(), button.getRect());
                    break;
                case FIELD:
                    FieldPart field = FieldPart.fromModel(card, (FieldModel) thisPart);
                    card.fields.addPart(field);
                    card.addSwingComponent(field.getComponent(), field.getRect());
                default:
            }
        }

        // Setup part cut, copy and paste
        card.setTransferHandler(new CardPartTransferHandler(card));

        // Setup the paint canvases
        card.foregroundCanvas = new UndoablePaintCanvas(card.cardModel.getCardImage());
        card.foregroundCanvas.addCanvasCommitObserver(card);
        card.backgroundCanvas = new UndoablePaintCanvas(card.getCardBackground().getBackgroundImage());
        card.backgroundCanvas.addCanvasCommitObserver(card);

        card.foregroundCanvas.setTransferHandler(new CanvasTransferHandler(card.foregroundCanvas, card));
        card.backgroundCanvas.setTransferHandler(new CanvasTransferHandler(card.backgroundCanvas, card));

        card.foregroundCanvas.setSize(stack.getWidth(), stack.getHeight());
        card.backgroundCanvas.setSize(stack.getWidth(), stack.getHeight());

        card.setLayer(card.foregroundCanvas, FOREGROUND_CANVAS_LAYER);
        card.add(card.foregroundCanvas);

        card.setLayer(card.backgroundCanvas, BACKGROUND_CANVAS_LAYER);
        card.add(card.backgroundCanvas);

        // Resize Swing component
        card.setMaximumSize(stack.getSize());
        card.setSize(stack.getWidth(), stack.getHeight());

        card.foregroundCanvas.getScaleProvider().addObserver((o, arg) -> card.setBackgroundVisible(((Double) arg) == 1.0));
        card.backgroundCanvas.getScaleProvider().addObserver((o, arg) -> card.setForegroundVisible(((Double) arg) == 1.0));

        // Fire property change observers on the parts (so that they can draw themselves in their correct initial state)
        for (ButtonPart thisButton : card.buttons.getParts()) {
            thisButton.getPartModel().notifyPropertyChangedObserver(thisButton);
        }

        for (FieldPart thisField : card.fields.getParts()) {
            thisField.getPartModel().notifyPropertyChangedObserver(thisField);
        }

        // Listen for image files that are dropped onto the card
        new FileDrop(card, files -> ImageImporter.importAsSelection(files[0]));

        ToolsContext.getInstance().isEditingBackgroundProvider().addObserver((oldValue, isEditingBackground) -> {
            card.setForegroundVisible(!(boolean) isEditingBackground);
        });

        return card;
    }

    /**
     * Imports an existing part (button or field) into this card. Note that this differs from {@link #addField(FieldPart)}
     * or {@link #addButton(ButtonPart)} in that a new ID for the part is generated before it is added to the card. This
     * method is typically used to "paste" a copied part from another card onto this card.
     *
     * @param part The part to be imported.
     * @return The newly imported part (identical to the given part, but with a new ID)
     * @throws HtException Thrown if an error occurs importing the part.
     */
    public Part importPart(Part part) throws HtException {

        if (part instanceof ButtonPart) {
            return importButton((ButtonPart) part);
        } else if (part instanceof FieldPart) {
            return importField((FieldPart) part);
        } else {
            throw new IllegalArgumentException("Bug! Unimplemented import of part type: " + part.getClass());
        }
    }

    /**
     * Imports an existing button into this card. Note that this differs from {@link #addButton(ButtonPart)} in that a
     * new ID for the part is generated before it is added to the card. This method is typically used to "paste" a
     * copied button from another card onto this card.
     *
     * @param part The button to be imported.
     * @return The newly imported button (identical to the given part, but with a new ID)
     * @throws HtException Thrown if an error occurs importing the part.
     */
    public ButtonPart importButton(ButtonPart part) throws HtException {
        ButtonModel model = (ButtonModel) Serializer.copy(part.getPartModel());
        model.defineProperty(AbstractPartModel.PROP_ID, new Value(buttons.getNextId()), true);

        ButtonPart newButton = ButtonPart.fromModel(this, model);
        addButton(newButton);
        return newButton;
    }

    /**
     * Imports an existing field into this card. Note that this differs from {@link #addField(FieldPart)} in that a
     * new ID for the part is generated before it is added to the card. This method is typically used to "paste" a
     * copied field from another card onto this card.
     *
     * @param part The field to be imported.
     * @return The newly imported field (identical to the given part, but with a new ID)
     * @throws HtException Thrown if an error occurs importing the part.
     */
    public FieldPart importField(FieldPart part) throws HtException {
        FieldModel model = (FieldModel) Serializer.copy(part.getPartModel());
        model.defineProperty(AbstractPartModel.PROP_ID, new Value(fields.getNextId()), true);

        FieldPart newField = FieldPart.fromModel(this, model);
        addField(newField);
        return newField;
    }

    /**
     * Adds a field to this card. Assumes that the field has a unique ID.
     * @param field The field to add to this card.
     * @throws PartException Thrown if an error occurs adding the field.
     */
    public void addField(FieldPart field) throws PartException {
        cardModel.addPart(field);
        fields.addPart(field);
        addSwingComponent(field.getComponent(), field.getRect());
        field.partOpened();
    }

    /**
     * Removes a field from this card. Has no effect if the field does not exist on the card.
     * @param field The field to be removed.
     */
    public void removeField(FieldPart field) {
        cardModel.removePart(field);
        fields.removePart(field);
        removeSwingComponent(field.getComponent());
        field.partClosed();
    }

    /**
     * Adds a button to this card. Assumes the button has a unique ID.
     * @param button The button to be added.
     * @throws PartException Thrown if an error occurs adding this button to the card.
     */
    public void addButton(ButtonPart button) throws PartException {
        cardModel.addPart(button);
        buttons.addPart(button);
        addSwingComponent(button.getComponent(), button.getRect());
        button.partOpened();
    }

    /**
     * Removes a button from this card. Has no effect if the button does not exist on the card.
     * @param button The button to be removed.
     */
    public void removeButton(ButtonPart button) {
        cardModel.removePart(button);
        buttons.removePart(button);
        removeSwingComponent(button.getComponent());
        button.partClosed();
    }

    /**
     * Removes a part (button or field) from this card. Has no effect of the part is not on this card.
     * @param part The part to be removed.
     */
    public void removePart(Part part) {
        if (part instanceof ButtonPart) {
            removeButton((ButtonPart) part);
        } else if (part instanceof FieldPart) {
            removeField((FieldPart) part);
        }
    }

    /**
     * Adds a new button (with default attributes) to this card. Represents the behavior of the user choosing
     * "New Button" from the Objects menu.
     */
    public void newButton() {
        try {
            ButtonPart newButton = ButtonPart.newButton(this);
            addButton(newButton);
            PartToolContext.getInstance().setSelectedPart(newButton);
        } catch (PartException ex) {
            throw new RuntimeException("Bug! Shouldn't be possible.", ex);
        }
    }

    /**
     * Adds a new field (with default attributes) to this card. Represents the behavior of the user choosing
     * "New Field" from the Objects menu.
     */
    public void newField() {
        try {
            FieldPart newField = FieldPart.newField(this);
            addField(newField);
            PartToolContext.getInstance().setSelectedPart(newField);
        } catch (PartException ex) {
            throw new RuntimeException("Bug! Shouldn't be possible.", ex);
        }
    }

    /**
     * Returns the part (button or field) represented by a given a HyperTalk part specifier.
     * @param ps The part specifier representing the part to fetch
     * @return The specified part
     * @throws PartException Thrown if no such part exists on this card.
     */
    public Part getPart(PartSpecifier ps) throws PartException {
        if (ps.type() == PartType.FIELD)
            return fields.getPart(ps);
        else if (ps.type() == PartType.BUTTON)
            return buttons.getPart(ps);
        else
            throw new RuntimeException("Bug! Unhandled part type.");
    }

    /**
     * Gets an unordered collection of buttons that exist on this card.
     * @return The buttons on this card.
     */
    public Collection<ButtonPart> getButtons() {
        return buttons.getParts();
    }

    /**
     * Gets an unordered collection of fields that exist on this card.
     * @return The fields on this card.
     */
    public Collection<FieldPart> getFields() {
        return fields.getParts();
    }

    /**
     * Gets a list of parts (buttons and field) that appear on this card, listed in their z-order (that is, the order
     * in which one is drawn atop another).
     * @return The z-ordered list of parts on this card.
     */
    public List<Part> getPartsInZOrder() {
        ArrayList<Part> joined = new ArrayList<>();
        joined.addAll(getButtons());
        joined.addAll(getFields());

        Comparator<Part> zOrderComparator = (o1, o2) -> new Integer(o1.getPartModel().getKnownProperty(AbstractPartModel.PROP_ZORDER).integerValue())
                .compareTo(o2.getPartModel().getKnownProperty(AbstractPartModel.PROP_ZORDER).integerValue());

        joined.sort(zOrderComparator);

        return joined;
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
    public UndoablePaintCanvas getCanvas() {
        return ToolsContext.getInstance().isEditingBackground() ? backgroundCanvas : foregroundCanvas;
    }

    /**
     * Determines if the foreground is presently visible. The foreground may be hidden when editing the background
     * or when certain scale operations are active.
     * @return True if the foreground is visible; false otherwise.
     */
    public boolean isForegroundVisible() {
        return foregroundCanvas.isVisible();
    }

    /**
     * Hides or shows the card foreground.
     * @param isVisible Shows the foreground when true; hides it otherwise.
     */
    private void setForegroundVisible(boolean isVisible) {
        foregroundCanvas.setVisible(isVisible);

        for (Component thisComponent : getComponentsInLayer(FOREGROUND_PARTS_LAYER)) {
            thisComponent.setVisible(isVisible);
        }

        // Notify the window manager than background editing changed
        WindowManager.getStackWindow().invalidateWindowTitle();
    }

    /**
     * Gets the zero-based location of this card in its stack.
     * @return The location of this card in the stack.
     */
    public int getCardIndexInStack() {
        return getStackModel().getIndexOfCard(this.getCardModel());
    }

    /**
     * Gets the data model associated with this card's stack.
     * @return The stack model for this card.
     */
    public StackModel getStackModel() {
        return stackModel;
    }

    /**
     * Hides or shows the background layer for this card.
     * @param isVisible True to show the background; false to hide it.
     */
    private void setBackgroundVisible(boolean isVisible) {
        backgroundCanvas.setVisible(isVisible);
    }

    /**
     * Returns the data model associated with this card. Note that backgrounds are shared across cards; mutating this
     * object may affect other cards in the stack, too.
     * @return The data model associated with this card's background.
     */
    public BackgroundModel getCardBackground() {
        return stackModel.getBackground(cardModel.getBackgroundId());
    }

    /**
     * Swap the Swing component associated with a given part (button or field) for a new component. This is useful
     * when changing button or field styles.
     *
     * @param forPart The part whose Swing component should be replaced.
     * @param oldButtonComponent The old Swing component associated with this part.
     * @param newButtonComponent The new Swing component to be used.
     */
    public void replaceSwingComponent(Part forPart, Component oldButtonComponent, Component newButtonComponent) {
        removeSwingComponent(oldButtonComponent);
        addSwingComponent(newButtonComponent, forPart.getRect());
    }

    /**
     * Removes a Swing component from this card's JLayeredPane.
     * @param component The component to remove.
     */
    private void removeSwingComponent(Component component) {
        remove(component);
        revalidate();
        repaint();
    }

    /**
     * Adds a Swing component to this card's JLayeredPane.
     * @param component The component to add.
     * @param bounds The component's desired location and size.
     */
    private void addSwingComponent(Component component, Rectangle bounds) {
        component.setBounds(bounds);
        setLayer(component, FOREGROUND_PARTS_LAYER);
        add(component);
        moveToFront(component);
        revalidate();
        repaint();
    }

    /**
     * The next available ID for a field on this card.
     * TODO: IDs should be global across the stack; not relative to the card.
     * @return The next available ID.
     */
    public int nextFieldId() {
        return fields.getNextId();
    }

    /**
     * The next available ID for a button on this card.
     * TODO: IDs should be global across the stack; not relative to the card.
     * @return The next available ID.
     */
    public int nextButtonId() {
        return buttons.getNextId();
    }

    /**
     * Indicates that the z-order of a part changed (and that components should be reordered on the pane according to
     * their new position).
     */
    public void onZOrderChanged() {
        SwingUtilities.invokeLater(() -> {
            for (Part thisPart : getPartsInZOrder()) {
                moveToBack(thisPart.getComponent());
            }
        });
    }

    @Override
    public void onCommit(PaintCanvas canvas, ChangeSet changeSet, BufferedImage canvasImage) {
        if (ToolsContext.getInstance().isEditingBackground()) {
            getCardBackground().setBackgroundImage(canvasImage);
        } else {
            cardModel.setCardImage(canvasImage);
        }
    }

    @Override
    public BufferedImage copySelection() {
        PaintTool activeTool = ToolsContext.getInstance().getPaintTool();
        if (activeTool instanceof AbstractSelectionTool) {
            return ((AbstractSelectionTool) activeTool).getSelectedImage();
        }

        return null;
    }

    @Override
    public void deleteSelection() {
        PaintTool activeTool = ToolsContext.getInstance().getPaintTool();
        if (activeTool instanceof AbstractSelectionTool) {
            ((AbstractSelectionTool) activeTool).deleteSelection();
        }
    }

    @Override
    public void pasteSelection(BufferedImage image) {
        int cardCenterX = getWidth() / 2;
        int cardCenterY = getHeight() / 2;

        SelectionTool tool = (SelectionTool) ToolsContext.getInstance().selectPaintTool(PaintToolType.SELECTION);
        tool.createSelection(image, new Point(cardCenterX - image.getWidth() / 2, cardCenterY - image.getHeight() / 2));
    }
}
