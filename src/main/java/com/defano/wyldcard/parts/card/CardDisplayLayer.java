package com.defano.wyldcard.parts.card;

import com.defano.hypertalk.ast.model.Owner;

/**
 * An enumeration of layers in the card view stack.
 */
public enum CardDisplayLayer {
    BACKGROUND_GRAPHICS(1, "background"),
    BACKGROUND_PARTS(2, "background"),
    CARD_GRAPHICS(3, "card"),
    CARD_PARTS(4, "card");

    private final int paneLayer;
    private final String hyperTalkName;

    CardDisplayLayer(int paneLayer, String hyperTalkName) {
        this.paneLayer = paneLayer;
        this.hyperTalkName = hyperTalkName;
    }

    public Owner asOwner() {
        return (this == BACKGROUND_GRAPHICS || this == BACKGROUND_PARTS) ? Owner.BACKGROUND : Owner.CARD;
    }

    public String getHyperTalkName() {
        return hyperTalkName;
    }

    public int getPaneLayer() {
        return paneLayer;
    }

    public static CardDisplayLayer fromPaneLayer(int layer) {
        for (CardDisplayLayer thisLayer : CardDisplayLayer.values()) {
            if (thisLayer.paneLayer == layer) {
                return thisLayer;
            }
        }

        throw new IllegalArgumentException("No such layer: " + layer);
    }
}
