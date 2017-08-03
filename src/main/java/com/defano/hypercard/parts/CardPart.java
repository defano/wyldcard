/*
 * CardPart
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts;

import com.defano.hypercard.serializer.Serializer;
import com.defano.hypercard.context.PartToolContext;
import com.defano.hypercard.context.PartsTable;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.gui.util.ThreadUtils;
import com.defano.hypercard.parts.clipboard.CardPartTransferHandler;
import com.defano.hypercard.parts.model.*;
import com.defano.hypercard.parts.model.ButtonModel;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.jmonet.canvas.ChangeSet;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.canvas.JMonetCanvas;
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
import java.util.Observable;
import java.util.Observer;

/**
 * The "controller" object representing a card in the stack.
 *
 * See {@link CardLayeredPane} for the view object.
 * See {@link CardModel} for the model object.
 */
public class CardPart extends CardLayeredPane implements Part, LayeredPartContainer, CanvasCommitObserver, CanvasTransferDelegate {

    private CardModel cardModel;
    private StackModel stackModel;

    private final PartsTable<FieldPart> fields = new PartsTable<>();
    private final PartsTable<ButtonPart> buttons = new PartsTable<>();

    private EditingBackgroundObserver editingBackgroundObserver = new EditingBackgroundObserver();
    private ForegroundScaleObserver foregroundScaleObserver = new ForegroundScaleObserver();
    private BackgroundScaleObserver backgroundScaleObserver = new BackgroundScaleObserver();

    private CardPart() {
        super();
        this.setLayout(null);
    }

    /**
     * Instantiates the CardPart at a specified location in a specified stack.
     *
     * @param cardIndex The location in the stack whose card should be returned.
     * @param stack The stack data model containing the card to return
     * @return The CardPart.
     * @throws HtException Thrown if an error occurs creating the card.
     */
    public static CardPart fromLocationInStack(int cardIndex, StackModel stack) throws HtException {
        CardPart card = new CardPart();
        card.cardModel = stack.getCardModel(cardIndex);
        card.stackModel = stack;

        // Add card parts to this card
        for (PartModel thisPart : card.cardModel.getPartModels()) {
            card.addPartFromModel(thisPart, CardLayer.CARD_PARTS);
        }

        // Add background parts to this card
        for (PartModel thisPart : card.getCardBackground().getPartModels()) {
            card.addPartFromModel(thisPart, CardLayer.BACKGROUND_PARTS);
        }

        // Setup part cut, copy and paste
        card.setTransferHandler(new CardPartTransferHandler(card));

        // Setup the foreground paint canvas
        card.setForegroundCanvas(new JMonetCanvas(card.cardModel.getCardImage()));
        card.getForegroundCanvas().addCanvasCommitObserver(card);
        card.getForegroundCanvas().setTransferHandler(new CanvasTransferHandler(card.getForegroundCanvas(), card));
        card.getForegroundCanvas().setSize(stack.getWidth(), stack.getHeight());

        // Setup the background paint canvas
        card.setBackgroundCanvas(new JMonetCanvas(card.getCardBackground().getBackgroundImage()));
        card.getBackgroundCanvas().addCanvasCommitObserver(card);
        card.getBackgroundCanvas().setTransferHandler(new CanvasTransferHandler(card.getBackgroundCanvas(), card));
        card.getBackgroundCanvas().setSize(stack.getWidth(), stack.getHeight());

        // Resize Swing component
        card.setMaximumSize(stack.getSize());
        card.setSize(stack.getWidth(), stack.getHeight());

        // Fire property change observers on the parts (so that they can draw themselves in their correct initial state)
        for (ButtonPart thisButton : card.buttons.getParts()) {
            thisButton.getPartModel().notifyPropertyChangedObserver(thisButton);
        }

        for (FieldPart thisField : card.fields.getParts()) {
            thisField.getPartModel().notifyPropertyChangedObserver(thisField);
        }

        return card;
    }

    /**
     * Imports an existing part (button or field) into this card.
     *
     * Note that this differs from {@link #addField(FieldPart, CardLayer)} or {@link #addButton(ButtonPart, CardLayer)}
     * in that a new ID for the part is generated before it is added to the card. This method is typically used to
     * "paste" a copied part from another card onto this card.
     *
     * @param part The part to be imported.
     * @return The newly imported part (identical to the given part, but with a new ID)
     * @throws HtException Thrown if an error occurs importing the part.
     */
    public Part importPart(Part part, CardLayer layer) throws HtException {
        if (part instanceof ButtonPart) {
            return importButton((ButtonPart) part, layer);
        } else if (part instanceof FieldPart) {
            return importField((FieldPart) part, layer);
        } else {
            throw new IllegalArgumentException("Bug! Unimplemented import of part type: " + part.getClass());
        }
    }

    /**
     * Removes a part (button or field) from this card. Has no effect if the part is not on this card.
     * @param part The part to be removed.
     */
    public void removePart(PartModel part) {
        if (part instanceof ButtonModel) {
            removeButton((ButtonModel) part);
        } else if (part instanceof FieldModel) {
            removeField((FieldModel) part);
        }
    }

    /**
     * Adds a new button (with default attributes) to this card. Represents the behavior of the user choosing
     * "New Button" from the Objects menu.
     */
    public void newButton() {
        try {
            CardLayer layer = CardLayerPart.getActivePartLayer();
            ButtonPart newButton = ButtonPart.newButton(this, layer.asOwner());
            addButton(newButton, layer);
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
            CardLayer layer = CardLayerPart.getActivePartLayer();
            FieldPart newField = FieldPart.newField(this, layer.asOwner());
            addField(newField, layer);
            PartToolContext.getInstance().setSelectedPart(newField);
        } catch (PartException ex) {
            throw new RuntimeException("Bug! Shouldn't be possible.", ex);
        }
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
     * Adds a part to a specified layer on this card based on its model.
     * @param thisPart The data model of the part to be added.
     * @param layer The layer of this card on which the part should be added.
     * @throws HtException Thrown if an error occurs adding the part.
     */
    private void addPartFromModel(PartModel thisPart, CardLayer layer) throws HtException {
        switch (thisPart.getType()) {
            case BUTTON:
                ButtonPart button = ButtonPart.fromModel(this, (ButtonModel) thisPart, layer.asOwner());
                buttons.addPart(button);
                addSwingComponent(button.getComponent(), button.getRect(), layer);
                break;
            case FIELD:
                FieldPart field = FieldPart.fromModel(this, (FieldModel) thisPart, layer.asOwner());
                fields.addPart(field);
                addSwingComponent(field.getComponent(), field.getRect(), layer);
                break;
            default:
                throw new IllegalStateException("Bug! Unimplemented part model: " + thisPart);
        }
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
        return ToolsContext.getInstance().isEditingBackground() ? getBackgroundCanvas() : getForegroundCanvas();
    }

    /**
     * Hides or shows the card foreground.
     * @param isVisible Shows the foreground when true; hides it otherwise.
     */
    @Override
    public void setForegroundVisible(boolean isVisible) {
        super.setForegroundVisible(isVisible);

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
        getBackgroundCanvas().setVisible(isVisible);
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
        CardLayer partLayer = getCardLayer(oldButtonComponent);
        removeSwingComponent(oldButtonComponent);
        addSwingComponent(newButtonComponent, forPart.getRect(), partLayer);
        forPart.partOpened();
    }

    /**
     * Indicates that the z-order of a part changed (and that components should be reordered on the pane according to
     * their new position).
     */
    public void onDisplayOrderChanged() {
        SwingUtilities.invokeLater(() -> {
            for (PartModel thisPart : getPartsInDisplayOrder()) {
                moveToBack(((CardLayerPart)getPart(thisPart)).getComponent());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCommit(PaintCanvas canvas, ChangeSet changeSet, BufferedImage canvasImage) {
        if (ToolsContext.getInstance().isEditingBackground()) {
            getCardBackground().setBackgroundImage(canvasImage);
        } else {
            cardModel.setCardImage(canvasImage);
        }
    }

    /** {@inheritDoc} */
    @Override
    public BufferedImage copySelection() {
        PaintTool activeTool = ToolsContext.getInstance().getPaintTool();
        if (activeTool instanceof AbstractSelectionTool) {
            return ((AbstractSelectionTool) activeTool).getSelectedImage();
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void deleteSelection() {
        PaintTool activeTool = ToolsContext.getInstance().getPaintTool();
        if (activeTool instanceof AbstractSelectionTool) {
            ((AbstractSelectionTool) activeTool).deleteSelection();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void pasteSelection(BufferedImage image) {
        int cardCenterX = getWidth() / 2;
        int cardCenterY = getHeight() / 2;

        SelectionTool tool = (SelectionTool) ToolsContext.getInstance().selectPaintTool(PaintToolType.SELECTION, false);
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
        BufferedImage screenshot = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            Graphics2D g = screenshot.createGraphics();
            CardPart.this.printAll(g);
            g.dispose();
        });

        return screenshot;
    }

    /** {@inheritDoc} */
    @Override
    public int getHeight() {
        return stackModel.getHeight();
    }

    /** {@inheritDoc} */
    @Override
    public int getWidth() {
        return stackModel.getWidth();
    }

    public void closeCard() {
        // Lets parts know they're about to go away
        notifyPartsClosing();

        // Remove their Swing components from the card to free memory
        removeAll();

        setTransferHandler(null);
        ToolsContext.getInstance().isEditingBackgroundProvider().deleteObserver(editingBackgroundObserver);
        getForegroundCanvas().dispose();
        getBackgroundCanvas().dispose();

        editingBackgroundObserver = null;
        foregroundScaleObserver = null;
        backgroundScaleObserver = null;

        super.dispose();
    }

    public void openCard() {
        ToolsContext.getInstance().isEditingBackgroundProvider().addObserver(editingBackgroundObserver);
        getForegroundCanvas().getScaleProvider().addObserver(foregroundScaleObserver);
        getBackgroundCanvas().getScaleProvider().addObserver(backgroundScaleObserver);
    }

    /**
     * Notify all parts in this container that they are closing (ostensibly because the container itself is closing).
     */
    private void notifyPartsClosing() {
        for (ButtonPart p : buttons.getParts()) {
            p.partClosed();
        }

        for (FieldPart p : fields.getParts()) {
            p.partClosed();
        }
    }

    /**
     * Imports an existing button into this card. Note that this differs from {@link #addButton(ButtonPart, CardLayer)} in that a
     * new ID for the part is generated before it is added to the card. This method is typically used to "paste" a
     * copied button from another card onto this card.
     *
     * @param part The button to be imported.
     * @return The newly imported button (identical to the given part, but with a new ID)
     * @throws HtException Thrown if an error occurs importing the part.
     */
    private ButtonPart importButton(ButtonPart part, CardLayer layer) throws HtException {
        ButtonModel model = (ButtonModel) Serializer.copy(part.getPartModel());
        model.defineProperty(PartModel.PROP_ID, new Value(stackModel.getNextButtonId()), true);

        ButtonPart newButton = ButtonPart.fromModel(this, model, layer.asOwner());
        addButton(newButton, layer);
        return newButton;
    }

    /**
     * Imports an existing field into this card. Note that this differs from {@link #addField(FieldPart, CardLayer)} in that a
     * new ID for the part is generated before it is added to the card. This method is typically used to "paste" a
     * copied field from another card onto this card.
     *
     * @param part The field to be imported.
     * @return The newly imported field (identical to the given part, but with a new ID)
     * @throws HtException Thrown if an error occurs importing the part.
     */
    private FieldPart importField(FieldPart part, CardLayer layer) throws HtException {
        FieldModel model = (FieldModel) Serializer.copy(part.getPartModel());
        model.defineProperty(PartModel.PROP_ID, new Value(stackModel.getNextFieldId()), true);

        FieldPart newField = FieldPart.fromModel(this, model, layer.asOwner());
        addField(newField, layer);
        return newField;
    }

    /**
     * Adds a field to this card. Assumes that the field has a unique ID.
     * @param field The field to add to this card.
     * @throws PartException Thrown if an error occurs adding the field.
     */
    private void addField(FieldPart field, CardLayer layer) throws PartException {
        if (layer == CardLayer.CARD_PARTS) {
            cardModel.addPartModel(field.getPartModel());
        } else if (layer == CardLayer.BACKGROUND_PARTS) {
            getCardBackground().addFieldModel((FieldModel) field.getPartModel());
        }

        fields.addPart(field);
        addSwingComponent(field.getComponent(), field.getRect(), layer);
        field.partOpened();
    }

    /**
     * Removes a field from this card. Has no effect if the field does not exist on the card.
     * @param fieldModel The field to be removed.
     */
    private void removeField(FieldModel fieldModel) {
        FieldPart field = fields.getPartForModel(fieldModel);
        cardModel.removePartModel(fieldModel);
        fields.removePart(field);
        removeSwingComponent(field.getComponent());
        field.partClosed();
    }

    /**
     * Adds a button to this card. Assumes the button has a unique ID.
     * @param button The button to be added.
     * @throws PartException Thrown if an error occurs adding this button to the card.
     */
    private void addButton(ButtonPart button, CardLayer layer) throws PartException {
        if (layer == CardLayer.CARD_PARTS) {
            cardModel.addPartModel(button.getPartModel());
        } else if (layer == CardLayer.BACKGROUND_PARTS) {
            getCardBackground().addButtonModel((ButtonModel) button.getPartModel());
        }

        buttons.addPart(button);
        addSwingComponent(button.getComponent(), button.getRect(), layer);
        button.partOpened();
    }

    /**
     * Removes a button from this card. Has no effect if the button does not exist on the card.
     * @param buttonModel The button to be removed.
     */
    private void removeButton(ButtonModel buttonModel) {
        ButtonPart button = buttons.getPartForModel(buttonModel);
        cardModel.removePartModel(buttonModel);
        buttons.removePart(button);
        removeSwingComponent(button.getComponent());
        button.partClosed();
    }

    /**
     * Removes a Swing component from this card's JLayeredPane.
     * @param component The component to remove.
     */
    private void removeSwingComponent(Component component) {
        remove(component);
        revalidate(); repaint();
    }

    /**
     * Adds a Swing component to this card's JLayeredPane.
     * @param component The component to add.
     * @param bounds The component's desired location and size.
     */
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
    public void partOpened() {
        // Nothing to do
    }

    /** {@inheritDoc} */
    @Override
    public void partClosed() {
        // Nothing to do
    }

    public Part getPart(PartModel partModel) {
        if (partModel instanceof FieldModel) {
            return fields.getPartForModel(partModel);
        } else if (partModel instanceof ButtonModel) {
            return buttons.getPartForModel(partModel);
        }

        throw new IllegalArgumentException("No part on this card matching model: " + partModel);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<PartModel> getParts() {
        ArrayList<PartModel> partModels = new ArrayList<>();
        for (ButtonPart thisButton : buttons.getParts()) {
            partModels.add(thisButton.getPartModel());
        }
        for (FieldPart thisField : fields.getParts()) {
            partModels.add(thisField.getPartModel());
        }
        return partModels;
    }

    private class BackgroundScaleObserver implements Observer {
        @Override
        public void update(Observable o, Object scale) {
            setForegroundVisible(((Double) scale) == 1.0);
        }
    }

    private class ForegroundScaleObserver implements Observer {
        @Override
        public void update(Observable o, Object scale) {
            setBackgroundVisible(((Double) scale) == 1.0);
        }
    }

    private class EditingBackgroundObserver implements Observer {
        @Override
        public void update(Observable o, Object isEditingBackground) {
            setForegroundVisible(!(boolean) isEditingBackground);
        }
    }

}
