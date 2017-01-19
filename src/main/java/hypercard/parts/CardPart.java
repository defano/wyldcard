/**
 * CardPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements a card part by extending the Swing panel object.
 */

package hypercard.parts;

import com.defano.jmonet.canvas.Canvas;
import com.defano.jmonet.canvas.CanvasCommitObserver;
import com.defano.jmonet.canvas.UndoableCanvas;
import hypercard.context.PartsTable;
import hypercard.context.ToolsContext;
import hypercard.parts.model.*;
import hypercard.parts.model.ButtonModel;
import hypertalk.ast.common.PartType;
import hypertalk.ast.containers.PartSpecifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class CardPart extends JLayeredPane implements ComponentListener, CanvasCommitObserver {

    private final static int BACKGROUND_CANVAS_LAYER = 0;
    private final static int BACKGROUND_PARTS_LAYER = 1;
    private final static int FOREGROUND_CANVAS_LAYER = 2;
    private final static int FOREGROUND_PARTS_LAYER = 3;

    private CardModel model;

    private PartsTable<FieldPart> fields = new PartsTable<>();
    private PartsTable<ButtonPart> buttons = new PartsTable<>();

    private UndoableCanvas foregroundCanvas;
    private UndoableCanvas backgroundCanvas;

    private transient StackModel stack;

    private CardPart() {
        super();

        this.setLayout(null);
        this.addComponentListener(this);
    }

    public static CardPart fromModel (CardModel model, StackModel stack) throws Exception {
        CardPart card = new CardPart();
        card.model = model;
        card.stack = stack;

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

        card.foregroundCanvas = new UndoableCanvas(model.getCardImage());
        card.foregroundCanvas.addObserver(card);
        card.backgroundCanvas = new UndoableCanvas(card.getCardBackground().getBackgroundImage());
        card.backgroundCanvas.addObserver(card);

        card.setLayer(card.foregroundCanvas, FOREGROUND_CANVAS_LAYER);
        card.add(card.foregroundCanvas);

        card.setLayer(card.backgroundCanvas, BACKGROUND_CANVAS_LAYER);
        card.add(card.backgroundCanvas);

        card.foregroundCanvas.setSize(stack.getWidth(), stack.getHeight());
        card.backgroundCanvas.setSize(stack.getWidth(), stack.getHeight());

        card.setMaximumSize(new Dimension(stack.getWidth(), stack.getHeight()));

        card.setSize(stack.getWidth(), stack.getHeight());

        ToolsContext.getInstance().isEditingBackgroundProvider().addObserver((oldValue, newValue) -> {
            ToolsContext.getInstance().reactivateTool(card.getCanvas());
            card.foregroundCanvas.setVisible(!(boolean)newValue);
        });

        return card;
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

    public UndoableCanvas getCanvas() {
        return ToolsContext.getInstance().isEditingBackground() ? backgroundCanvas : foregroundCanvas;
    }

    public BackgroundModel getCardBackground() {
        return stack.getBackground(model.getBackgroundId());
    }

    private void removeSwingComponent (Component component) {
        remove(component);
        revalidate();
        repaint();
    }

    private void addSwingComponent (Component component, Rectangle bounds) {
        component.setBounds(bounds);
        setLayer(component, FOREGROUND_PARTS_LAYER);
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
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {
        stack.fireOnCardOpened(this);
    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void onCommit(Canvas canvas, BufferedImage committedElement, BufferedImage canvasImage) {
        if (ToolsContext.getInstance().isEditingBackground()) {
            getCardBackground().setBackgroundImage(canvasImage);
        } else {
            model.setCardImage(canvasImage);
        }
    }
}
