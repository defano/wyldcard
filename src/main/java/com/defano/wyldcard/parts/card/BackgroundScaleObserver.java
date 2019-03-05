package com.defano.wyldcard.parts.card;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import io.reactivex.functions.Consumer;

import javax.swing.*;

class BackgroundScaleObserver implements Consumer<Double> {
    private CardPart cardPart;

    public BackgroundScaleObserver(CardPart cardPart) {
        this.cardPart = cardPart;
    }

    @Override
    public void accept(Double scale) {
        SwingUtilities.invokeLater(() -> cardPart.setPartsOnLayerVisible(new ExecutionContext(), Owner.BACKGROUND, (scale) == 1.0));
    }
}
