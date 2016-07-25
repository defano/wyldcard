package hypercard.parts.model;

import hypercard.context.PartsTable;
import hypercard.parts.*;
import hypertalk.ast.common.PartType;
import hypertalk.ast.containers.PartSpecifier;

import java.util.ArrayList;
import java.util.Collection;

public class CardModel {

    private Collection<PartModel> parts;

    private transient PartsTable<FieldPart> fields;
    private transient PartsTable<ButtonPart> buttons;

    private CardModel () {
        fields = new PartsTable<>();
        buttons = new PartsTable<>();
        parts = new ArrayList<>();
    }

    public static CardModel emptyCardModel () {
        return new CardModel();
    }

    public void sendPartOpened () {
        buttons.sendPartOpened();
        fields.sendPartOpened();
    }

    public Collection<PartModel> getPartModels() {
        return parts;
    }

    public Part getPart (PartSpecifier ps) throws PartException {
        if (ps.type() == PartType.FIELD)
            return fields.getPart(ps);
        else if (ps.type() == PartType.BUTTON)
            return buttons.getPart(ps);
        else
            throw new RuntimeException("Unhandled part type");
    }

    public void removePart (Part part) {
        switch (part.getType()) {
            case BUTTON:
                buttons.removePart((ButtonPart) part);
                break;
            case FIELD:
                fields.removePart((FieldPart) part);
                break;
        }

        parts.remove(part.getPartModel());
    }

    public void addPart (Part part) throws PartException {
        switch (part.getType()) {
            case BUTTON:
                buttons.addPart((ButtonPart) part);
                break;
            case FIELD:
                fields.addPart((FieldPart) part);
                break;
            default:
                throw new PartException("Unsupported part type: " + part.getType());
        }

        parts.add(part.getPartModel());
    }

    public int nextFieldId () {
        return fields.getNextId();
    }

    public int nextButtonId () {
        return buttons.getNextId();
    }
}
