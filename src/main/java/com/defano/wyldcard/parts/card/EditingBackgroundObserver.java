package com.defano.wyldcard.parts.card;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
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

        ThreadUtils.invokeAndWaitAsNeeded(() -> cardPart.setForegroundVisible(new ExecutionContext(), !isEditingBackground));
    }
}
