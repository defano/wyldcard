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

    public static CardPart fromModel(int cardIndex, StackModel stack) throws Exception {
        CardPart card = new CardPart();
        card.cardModel = stack.getCardModel(cardIndex);
        card.stackModel = stack;

        // Add parts to this card
        for (AbstractPartModel thisPart : card.cardModel.getPartModels()) {
            switch (thisPart.getType()) {
                case BUTTON:
                    ButtonPart button = ButtonPart.fromModel(card, (com.defano.hypercard.parts.model.ButtonModel) thisPart);
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

    public Part importPart(Part part) throws Exception {

        if (part instanceof ButtonPart) {
            return importButton((ButtonPart) part);
        } else if (part instanceof FieldPart) {
            return importField((FieldPart) part);
        } else {
            throw new IllegalArgumentException("Bug! Unimplemented import of part type: " + part.getClass());
        }
    }

    public ButtonPart importButton(ButtonPart part) throws Exception {
        ButtonModel model = (ButtonModel) Serializer.copy(part.getPartModel());
        model.defineProperty(AbstractPartModel.PROP_ID, new Value(buttons.getNextId()), true);

        ButtonPart newButton = ButtonPart.fromModel(this, model);
        addButton(newButton);
        return newButton;
    }

    public FieldPart importField(FieldPart part) throws Exception {
        FieldModel model = (FieldModel) Serializer.copy(part.getPartModel());
        model.defineProperty(AbstractPartModel.PROP_ID, new Value(fields.getNextId()), true);

        FieldPart newField = FieldPart.fromModel(this, model);
        addField(newField);
        return newField;
    }

    public void addField(FieldPart field) throws PartException {
        cardModel.addPart(field);
        fields.addPart(field);
        addSwingComponent(field.getComponent(), field.getRect());
        field.partOpened();
    }

    public void removeField(FieldPart field) {
        cardModel.removePart(field);
        fields.removePart(field);
        removeSwingComponent(field.getComponent());
        field.partClosed();
    }

    public void addButton(ButtonPart button) throws PartException {
        cardModel.addPart(button);
        buttons.addPart(button);
        addSwingComponent(button.getComponent(), button.getRect());
        button.partOpened();
    }

    public void removeButton(ButtonPart button) {
        cardModel.removePart(button);
        buttons.removePart(button);
        removeSwingComponent(button.getComponent());
        button.partClosed();
    }

    public void removePart(Part part) {
        if (part instanceof ButtonPart) {
            removeButton((ButtonPart) part);
        } else if (part instanceof FieldPart) {
            removeField((FieldPart) part);
        }
    }

    public void newButton() {
        try {
            ButtonPart newButton = ButtonPart.newButton(this);
            addButton(newButton);
            PartToolContext.getInstance().setSelectedPart(newButton);
        } catch (PartException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void newField() {
        try {
            FieldPart newField = FieldPart.newField(this);
            addField(newField);
            PartToolContext.getInstance().setSelectedPart(newField);
        } catch (PartException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Part getPart(PartSpecifier ps) throws PartException {
        if (ps.type() == PartType.FIELD)
            return fields.getPart(ps);
        else if (ps.type() == PartType.BUTTON)
            return buttons.getPart(ps);
        else
            throw new RuntimeException("Unhandled part type");
    }

    public Collection<ButtonPart> getButtons() {
        return buttons.getParts();
    }

    public Collection<FieldPart> getFields() {
        return fields.getParts();
    }

    public List<Part> getPartsInZOrder() {
        ArrayList<Part> joined = new ArrayList<>();
        joined.addAll(getButtons());
        joined.addAll(getFields());

        Comparator<Part> zOrderComparator = (o1, o2) -> new Integer(o1.getPartModel().getKnownProperty(AbstractPartModel.PROP_ZORDER).integerValue())
                .compareTo(o2.getPartModel().getKnownProperty(AbstractPartModel.PROP_ZORDER).integerValue());

        joined.sort(zOrderComparator);

        return joined;
    }

    public CardModel getCardModel() {
        return cardModel;
    }

    public UndoablePaintCanvas getCanvas() {
        return ToolsContext.getInstance().isEditingBackground() ? backgroundCanvas : foregroundCanvas;
    }

    public boolean isForegroundVisible() {
        return foregroundCanvas.isVisible();
    }

    private void setForegroundVisible(boolean isVisible) {
        foregroundCanvas.setVisible(isVisible);

        for (Component thisComponent : getComponentsInLayer(FOREGROUND_PARTS_LAYER)) {
            thisComponent.setVisible(isVisible);
        }

        // Notify the window manager than background editing changed
        WindowManager.getStackWindow().invalidateWindowTitle();
    }

    public int getCardIndexInStack() {
        return getStackModel().getIndexOfCard(this.getCardModel());
    }

    public StackModel getStackModel() {
        return stackModel;
    }

    private void setBackgroundVisible(boolean isVisible) {
        backgroundCanvas.setVisible(isVisible);
    }

    public BackgroundModel getCardBackground() {
        return stackModel.getBackground(cardModel.getBackgroundId());
    }

    public void invalidateSwingComponent(Part forPart, Component oldButtonComponent, Component newButtonComponent) {
        removeSwingComponent(oldButtonComponent);
        addSwingComponent(newButtonComponent, forPart.getRect());
    }

    private void removeSwingComponent(Component component) {
        remove(component);
        revalidate();
        repaint();
    }

    private void addSwingComponent(Component component, Rectangle bounds) {
        component.setBounds(bounds);
        setLayer(component, FOREGROUND_PARTS_LAYER);
        add(component);
        moveToFront(component);
        revalidate();
        repaint();
    }

    public int nextFieldId() {
        return fields.getNextId();
    }

    public int nextButtonId() {
        return buttons.getNextId();
    }

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
