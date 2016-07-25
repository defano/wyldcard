/**
 * CardPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements a card part by extending the Swing panel object.
 */

package hypercard.parts;

import hypercard.gui.menu.context.CardContextMenu;
import hypercard.parts.model.CardModel;
import hypercard.parts.model.PartModel;
import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.containers.PartSpecifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CardPart extends JPanel implements MouseListener {

    private CardModel model;

    private CardPart() {
        super();

        this.setComponentPopupMenu(new CardContextMenu(this));
        this.addMouseListener(this);
        this.setLayout(null);
    }

    public static CardPart fromModel (CardModel model) throws Exception {
        CardPart card = new CardPart();
        card.model = model;

        for (PartModel thisPart : model.getPartModels()) {
            switch (thisPart.getType()) {
                case BUTTON:
                    ButtonPart button = ButtonPart.fromModel(card, thisPart);
                    card.addSwingComponent(button, button.getRect());
                    break;
                case FIELD:
                    FieldPart field = FieldPart.fromModel(card, thisPart);
                    card.addSwingComponent(field, field.getRect());
                    default:
            }
        }

        return card;
    }

    public void partOpened() {
        this.setComponentPopupMenu(new CardContextMenu(this));
        model.sendPartOpened();
    }

    public void addField(FieldPart field) throws PartException {
        model.addPart(field);
        addSwingComponent(field, field.getRect());
    }

    public void removeField(FieldPart field) {
        model.removePart(field);
        removeSwingComponent(field);
    }

    public void addButton(ButtonPart button) throws PartException {
        model.addPart(button);
        addSwingComponent(button, button.getRect());
    }

    public void removeButton(ButtonPart button) {
        model.removePart(button);
        removeSwingComponent(button);
    }

    public Part getPart(PartSpecifier ps) throws PartException {
        return model.getPart(ps);
    }

    private void removeSwingComponent (Component component) {
        remove(component);
        revalidate();
        repaint();
    }

    private void addSwingComponent (Component component, Rectangle bounds) {
        component.setBounds(bounds);
        add(component);
        revalidate();
        repaint();
    }

    public int nextButtonId() {
        return model.nextButtonId();
    }

    public int nextFieldId() {
        return model.nextFieldId();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        RuntimeEnv.getRuntimeEnv().setTheMouse(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        RuntimeEnv.getRuntimeEnv().setTheMouse(false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}
}
