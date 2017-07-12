package com.defano.hypercard.parts;

public enum CardLayer {
    BACKGROUND_GRAPHICS(1, "Background"),
    BACKGROUND_PARTS(2, "Background"),
    CARD_GRAPHICS(3, "Card"),
    CARD_PARTS(4, "Card");

    public final int paneLayer;
    public final String friendlyName;

    CardLayer(int paneLayer, String friendlyName) {
        this.paneLayer = paneLayer;
        this.friendlyName = friendlyName;
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
