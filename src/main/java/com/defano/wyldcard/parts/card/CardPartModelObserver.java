package com.defano.wyldcard.parts.card;

import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

class CardPartModelObserver implements CardModelObserver {
    private CardPart cardPart;

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
