package com.defano.hypercard.parts;

public enum CardLayer {
    BACKGROUND_GRAPHICS(0),
    BACKGROUND_PARTS(1),
    CARD_GRAPHICS(2),
    CARD_PARTS(3);

    public final int paneLayer;

    CardLayer(int paneLayer) {
        this.paneLayer = paneLayer;
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
