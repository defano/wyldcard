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
import hypertalk.ast.common.PartType;
import hypertalk.ast.containers.PartSpecifier;

import javax.swing.*;
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

    public static CardPart newCard () {
        CardPart cardPart = new CardPart();
        cardPart.model = CardModel.emptyCardModel();
        return cardPart;
    }

    public static CardPart fromModel (CardModel model) throws Exception {
        CardPart card = new CardPart();
        card.model = model;

        for (PartModel thisPart : model.getPartModels()) {
            switch (thisPart.getType()) {
                case BUTTON:
                    ButtonPart button = ButtonPart.fromModel(card, thisPart);
                    card.add(button);
                    button.setBounds(button.getRect());
                    card.validate();
                    break;
                case FIELD:
                    FieldPart field = FieldPart.fromModel(card, thisPart);
                    card.add(field);
                    field.setBounds(field.getRect());
                    card.validate();
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

        this.add(field);
        field.setBounds(field.getRect());
        this.validate();
    }

    public void removeField(FieldPart field) {
        model.removePart(field);

        this.remove(field);
        this.validate();
        this.repaint();
    }

    public void addButton(ButtonPart button) throws PartException {
        model.addPart(button);

        this.add(button);
        button.setBounds(button.getRect());
        this.validate();
    }

    public void removeButton(ButtonPart button) {
        model.removePart(button);

        this.remove(button);
        this.validate();
        this.repaint();
    }

    public Part getPart(PartSpecifier ps) throws PartException {
        return model.getPart(ps);
    }

    public CardModel getCardModel() {
        return model;
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
