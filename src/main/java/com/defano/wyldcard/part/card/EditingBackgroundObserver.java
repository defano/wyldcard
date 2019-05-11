package com.defano.wyldcard.part.card;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import io.reactivex.functions.Consumer;

class EditingBackgroundObserver implements Consumer<Boolean> {
    private CardPart cardPart;

    public EditingBackgroundObserver(CardPart cardPart) {
        this.cardPart = cardPart;
    }

    @Override
    public void accept(Boolean isEditingBackground) {
        if (cardPart.getForegroundCanvas() != null && cardPart.getForegroundCanvas().getScale() != 1.0) {
            cardPart.getForegroundCanvas().setScale(1.0);
        }

        if (cardPart.getBackgroundCanvas() != null && cardPart.getBackgroundCanvas().getScale() != 1.0) {
            cardPart.getBackgroundCanvas().setScale(1.0);
        }

        Invoke.onDispatch(() -> cardPart.setEditingBackground(new ExecutionContext(), isEditingBackground));
    }
}
