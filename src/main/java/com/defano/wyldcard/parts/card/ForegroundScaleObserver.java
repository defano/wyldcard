package com.defano.wyldcard.parts.card;

import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import io.reactivex.functions.Consumer;

class ForegroundScaleObserver implements Consumer<Double> {
    private CardPart cardPart;

    public ForegroundScaleObserver(CardPart cardPart) {
        this.cardPart = cardPart;
    }

    @Override
    public void accept(Double scale) {
        Invoke.onDispatch(() -> {
            cardPart.setPartsVisible(new ExecutionContext(), Owner.CARD, scale == 1.0);
            cardPart.setPartsVisible(new ExecutionContext(), Owner.BACKGROUND, scale == 1.0);
            cardPart.setBackgroundVisible(new ExecutionContext(), scale == 1.0);
        });
    }
}
