package com.defano.hypercard.parts.card;

import com.defano.hypertalk.ast.common.Owner;

/**
 * An enumeration of layers in the card view stack.
 */
public enum CardLayer {
    BACKGROUND_GRAPHICS(1, "background"),
    BACKGROUND_PARTS(2, "background"),
    CARD_GRAPHICS(3, "card"),
    CARD_PARTS(4, "card");

    public final int paneLayer;
    public final String hyperTalkName;

    CardLayer(int paneLayer, String hyperTalkName) {
        this.paneLayer = paneLayer;
        this.hyperTalkName = hyperTalkName;
    }

    public Owner asOwner() {
        return (this == BACKGROUND_GRAPHICS || this == BACKGROUND_PARTS) ? Owner.BACKGROUND : Owner.CARD;
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
