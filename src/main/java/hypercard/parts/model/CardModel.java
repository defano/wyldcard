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

    private CardModel () {
        buttonModels = new ArrayList<>();
        fieldModels = new ArrayList<>();
    }

    public static CardModel emptyCardModel () {
        return new CardModel();
    }

    public Collection<AbstractPartModel> getPartModels() {
        List<AbstractPartModel> partModels = new ArrayList<>();
        partModels.addAll(buttonModels);
        partModels.addAll(fieldModels);
        return partModels;
    }

    public void removePart (Part part) {
        switch (part.getType()) {
            case BUTTON:
                buttonModels.remove(part.getPartModel());
                break;
            case FIELD:
                fieldModels.remove(part.getPartModel());
                break;
        }
    }

    public void addPart (Part part) throws PartException {
        switch (part.getType()) {
            case BUTTON:
                buttonModels.add((ButtonModel) part.getPartModel());
                break;
            case FIELD:
                fieldModels.add((FieldModel) part.getPartModel());
                break;
            default:
                throw new PartException("Unsupported part type: " + part.getType());
        }
    }
}
