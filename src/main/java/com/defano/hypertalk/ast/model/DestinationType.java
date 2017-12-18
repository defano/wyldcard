package com.defano.hypertalk.ast.model;

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
