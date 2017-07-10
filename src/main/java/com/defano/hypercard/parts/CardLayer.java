package com.defano.hypercard.parts;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum CardLayer {
    BKGND_GRAPHICS(0, 0),
    BKGND_PARTS(2, 1),
    CARD_GRAPHICS(1, 2),
    CARD_PARTS(3, 3);

    public final int paneLayer;
    public final int drawLayer;

    CardLayer(int paneLayer, int drawLayer) {
        this.paneLayer = paneLayer;
        this.drawLayer = drawLayer;
    }

    public static List<CardLayer> inDrawOrder() {
        List<CardLayer> values = Arrays.asList(CardLayer.values());
        values.sort(Comparator.comparingInt(o -> o.drawLayer));
        return values;
    }

    public static CardLayer fromPaneLayer(int layer) {
        for (CardLayer thisLayer : CardLayer.values()) {
            if (thisLayer.paneLayer == layer) {
                return thisLayer;
            }
        }

        throw new IllegalArgumentException("No such layer: " + layer);
    }
}
