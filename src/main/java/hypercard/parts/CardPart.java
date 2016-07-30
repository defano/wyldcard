/**
 * CardPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements a card part by extending the Swing panel object.
 */

package hypercard.parts;

import hypercard.context.PartsTable;
import hypercard.gui.menu.context.CardContextMenu;
import hypercard.parts.model.ButtonModel;
import hypercard.parts.model.CardModel;
import hypercard.parts.model.FieldModel;
import hypercard.parts.model.AbstractPartModel;
import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.PartType;
import hypertalk.ast.containers.PartSpecifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CardPart extends JPanel implements MouseListener {

    private CardModel model;

    private PartsTable<FieldPart> fields = new PartsTable<>();
    private PartsTable<ButtonPart> buttons = new PartsTable<>();

    private CardPart() {
        super();

        this.setComponentPopupMenu(new CardContextMenu(this));
        this.addMouseListener(this);
        this.setLayout(null);
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

        return card;
    }

    public void partOpened() {
        this.setComponentPopupMenu(new CardContextMenu(this));
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

    public int nextFieldId () {
        return fields.getNextId();
    }

    public int nextButtonId () {
        return buttons.getNextId();
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
