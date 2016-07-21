package hypercard.parts.model;

import hypercard.parts.ButtonPart;
import hypercard.parts.CardPart;
import hypercard.parts.FieldPart;
import hypercard.parts.Part;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CardModel {

    private Collection<PartModel> parts = new HashSet<>();

    public void addParts (Collection<? extends Part> parts) {
        this.parts.addAll(parts.stream().map(Part::getPartModel).collect(Collectors.toList()));
    }

    public void createPartsInCard(CardPart card) throws Exception {
        for (PartModel thisModel : parts) {
            switch (thisModel.getType()) {
                case BUTTON:
                    card.addButton(ButtonPart.fromModel(card, thisModel));
                    break;
                case FIELD:
                    card.addField(FieldPart.fromModel(card, thisModel));
                    break;
            }
        }
    }

}
