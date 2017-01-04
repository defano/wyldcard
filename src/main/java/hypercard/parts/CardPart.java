/**
 * CardPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements a card part by extending the Swing panel object.
 */

package hypercard.parts;

import hypercard.context.PartsTable;
import hypercard.parts.model.AbstractPartModel;
import hypercard.parts.model.ButtonModel;
import hypercard.parts.model.CardModel;
import hypercard.parts.model.FieldModel;
import hypercard.paint.canvas.*;
import hypercard.paint.canvas.Canvas;
import hypertalk.ast.common.PartType;
import hypertalk.ast.containers.PartSpecifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class CardPart extends JLayeredPane implements ComponentListener, CanvasObserver {

    private final static int CANVAS_LAYER = 0;
    private final static int PARTS_LAYER = 1;

    private CardModel model;

    private PartsTable<FieldPart> fields = new PartsTable<>();
    private PartsTable<ButtonPart> buttons = new PartsTable<>();
    private UndoableCanvas canvas;

    private CardPart() {
        super();

        this.setLayout(null);
        this.addComponentListener(this);
    }

    public static CardPart fromModel (CardModel model) throws Exception {
        CardPart card = new CardPart();
        card.model = model;

        for (AbstractPartModel thisPart : model.getPartModels()) {
            switch (thisPart.getType()) {
                case BUTTON:
                    ButtonPart button = ButtonPart.fromModel(card, (ButtonModel) thisPart);
                    card.buttons.addPart(button);
                    card.addSwingComponent(button, button.getRect());
                    break;
                case FIELD:
                    FieldPart field = FieldPart.fromModel(card, (FieldModel) thisPart);
                    card.fields.addPart(field);
                    card.addSwingComponent(field, field.getRect());
                    default:
            }
        }

        card.canvas = new UndoableCanvas(model.getCardImage());
        card.canvas.addObserver(card);
        card.setLayer(card.canvas, CANVAS_LAYER);
        card.add(card.canvas);

        return card;
    }

    public void partOpened() {
    }

    public void addField(FieldPart field) throws PartException {
        model.addPart(field);
        fields.addPart(field);
        addSwingComponent(field, field.getRect());
    }

    public void removeField(FieldPart field) {
        model.removePart(field);
        fields.removePart(field);
        removeSwingComponent(field);
    }

    public void addButton(ButtonPart button) throws PartException {
        model.addPart(button);
        buttons.addPart(button);
        addSwingComponent(button, button.getRect());
    }

    public void removeButton(ButtonPart button) {
        model.removePart(button);
        buttons.removePart(button);
        removeSwingComponent(button);
    }

    public Part getPart(PartSpecifier ps) throws PartException {
        if (ps.type() == PartType.FIELD)
            return fields.getPart(ps);
        else if (ps.type() == PartType.BUTTON)
            return buttons.getPart(ps);
        else
            throw new RuntimeException("Unhandled part type");
    }

    public UndoableCanvas getForegroundCanvas() {
        return canvas;
    }

    private void removeSwingComponent (Component component) {
        remove(component);
        revalidate();
        repaint();
    }

    private void addSwingComponent (Component component, Rectangle bounds) {
        component.setBounds(bounds);
        setLayer(component, PARTS_LAYER);
        add(component);
        revalidate();
        repaint();
    }

    public int nextFieldId () {
        return fields.getNextId();
    }

    public int nextButtonId () {
        return buttons.getNextId();
    }


    @Override
    public void componentResized(ComponentEvent e) {
        canvas.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {
        canvas.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void onCommit(Canvas canvas, BufferedImage committedElement, BufferedImage canvasImage) {
        model.setCardImage(canvasImage);
    }
}
