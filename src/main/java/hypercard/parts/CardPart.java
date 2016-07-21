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
import hypercard.parts.model.CardModel;
import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.PartType;
import hypertalk.ast.containers.PartSpecifier;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CardPart extends JPanel implements MouseListener {

    private PartsTable<FieldPart> fields = new PartsTable<>();
    private PartsTable<ButtonPart> buttons = new PartsTable<>();

    private CardPart() {
        super();

        this.setComponentPopupMenu(new CardContextMenu(this));
        this.addMouseListener(this);
        this.setLayout(null);
    }

    public static CardPart newCard () {
        return new CardPart();
    }

    public static CardPart fromModel (CardModel model) throws Exception {
        CardPart card = new CardPart();
        model.createPartsInCard(card);

        return card;
    }

    public void partOpened() {
        this.setComponentPopupMenu(new CardContextMenu(this));

        fields.sendPartOpened();
        buttons.sendPartOpened();
    }

    public void addField(FieldPart field) throws PartException {
        fields.addPart(field);

        this.add(field);
        field.setBounds(field.getRect());
        this.validate();
    }

    public void removeField(FieldPart field) {
        fields.removePart(field);

        this.remove(field);
        this.validate();
        this.repaint();
    }

    public void addButton(ButtonPart button) throws PartException {
        buttons.addPart(button);

        this.add(button);
        button.setBounds(button.getRect());
        this.validate();
    }

    public void removeButton(ButtonPart button) {
        buttons.removePart(button);

        this.remove(button);
        this.validate();
        this.repaint();
    }

    public Part getPart(PartSpecifier ps) throws PartException {
        if (ps.type() == PartType.FIELD)
            return fields.getPart(ps);
        else if (ps.type() == PartType.BUTTON)
            return buttons.getPart(ps);
        else
            throw new RuntimeException("Unhandled part type");
    }

    public CardModel getCardModel() {
        CardModel model = new CardModel();
        model.addParts(buttons.getParts());
        model.addParts(fields.getParts());
        return model;
    }

    public int nextButtonId() {
        return buttons.getNextId();
    }

    public int nextFieldId() {
        return fields.getNextId();
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
