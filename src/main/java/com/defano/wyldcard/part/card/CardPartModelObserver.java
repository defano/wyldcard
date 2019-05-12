package com.defano.wyldcard.part.card;

import com.defano.wyldcard.part.button.ButtonModel;
import com.defano.wyldcard.part.field.FieldModel;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;

class CardPartModelObserver implements CardModelObserver {

    private final CardPart cardPart;

    public CardPartModelObserver(CardPart cardPart) {
        this.cardPart = cardPart;
    }

    @Override
    public void onPartRemoved(ExecutionContext context, PartModel removedPartModel) {
        if (removedPartModel instanceof ButtonModel) {
            cardPart.closeButton(context, (ButtonModel) removedPartModel);
        } else if (removedPartModel instanceof FieldModel) {
            cardPart.closeField(context, (FieldModel) removedPartModel);
        }
    }
}
