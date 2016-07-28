package hypercard.parts.model;

import hypercard.context.PartsTable;
import hypercard.parts.*;
import hypertalk.ast.common.PartType;
import hypertalk.ast.containers.PartSpecifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CardModel {

    private Collection<ButtonModel> buttonModels;
    private Collection<FieldModel> fieldModels;

    private transient PartsTable<FieldPart> fields;
    private transient PartsTable<ButtonPart> buttons;

    private CardModel () {
        fields = new PartsTable<>();
        buttons = new PartsTable<>();
        buttonModels = new ArrayList<>();
        fieldModels = new ArrayList<>();
    }

    public static CardModel emptyCardModel () {
        return new CardModel();
    }

    public void sendPartOpened () {
        buttons.sendPartOpened();
        fields.sendPartOpened();
    }

    public Collection<AbstractPartModel> getPartModels() {
        List<AbstractPartModel> partModels = new ArrayList<>();
        partModels.addAll(buttonModels);
        partModels.addAll(fieldModels);
        return partModels;
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
                buttonModels.remove(part.getPartModel());
                break;
            case FIELD:
                fields.removePart((FieldPart) part);
                fieldModels.remove(part.getPartModel());
                break;
        }
    }

    public void addPart (Part part) throws PartException {
        switch (part.getType()) {
            case BUTTON:
                buttons.addPart((ButtonPart) part);
                buttonModels.add((ButtonModel) part.getPartModel());
                break;
            case FIELD:
                fields.addPart((FieldPart) part);
                fieldModels.add((FieldModel) part.getPartModel());
                break;
            default:
                throw new PartException("Unsupported part type: " + part.getType());
        }
    }

    public int nextFieldId () {
        return fields.getNextId();
    }

    public int nextButtonId () {
        return buttons.getNextId();
    }
}
