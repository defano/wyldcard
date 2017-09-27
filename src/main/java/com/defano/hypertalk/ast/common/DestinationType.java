/*
 * DestinationType
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.common;

public enum DestinationType {
    CARD, BACKGROUND;

    public PartType asPartType() {
        switch (this) {
            case CARD:
                return PartType.CARD;
            case BACKGROUND:
                return PartType.BACKGROUND;
        }

        throw new IllegalStateException("Bug! Unimplemented destination type: " + this);
    }
}
