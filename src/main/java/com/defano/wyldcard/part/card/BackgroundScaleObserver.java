package com.defano.wyldcard.part.card;

import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.wyldcard.runtime.ExecutionContext;
import io.reactivex.functions.Consumer;

import javax.swing.*;

class BackgroundScaleObserver implements Consumer<Double> {
    private CardPart cardPart;

    public BackgroundScaleObserver(CardPart cardPart) {
        this.cardPart = cardPart;
    }

    @Override
    public void accept(Double scale) {
        SwingUtilities.invokeLater(() -> cardPart.setPartsVisible(new ExecutionContext(), Owner.BACKGROUND, (scale) == 1.0));
    }
}
