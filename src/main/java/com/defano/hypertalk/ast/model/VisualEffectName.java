package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.jsegue.SegueName;

import java.util.Arrays;

public enum VisualEffectName {

    DISSOLVE("dissolve"),
    BARN_DOOR("barndoor"),
    CHECKERBOARD("checkerboard"),
    IRIS("iris"),
    PLAIN("plain"),
    PUSH("push"),
    SCROLL("scroll"),
    SHRINK("shrinkto"),
    STRETCH("stretchfrom"),
    VENETIAN_BLINDS("venetianblinds"),
    WIPE("wipe"),
    ZOOM("zoom");

    private final String hypertalkName;

    VisualEffectName(String hypertalkName) {
        this.hypertalkName = hypertalkName;
    }

    public static VisualEffectName fromHypertalkName(String hypertalkName) throws HtSemanticException {
        String name = hypertalkName.toLowerCase().replaceAll("\\s+", "");

        return Arrays.stream(values())
                .filter(v -> v.hypertalkName.equals(name))
                .findFirst()
                .orElseThrow(() -> new HtSemanticException("Not the name of a visual effect."));
    }

    public SegueName toSegueName(VisualEffectDirection direction) throws HtSemanticException {
        switch (this) {
            case DISSOLVE:
                return SegueName.DISSOLVE;
            case BARN_DOOR:
                if (direction == VisualEffectDirection.OPEN) {
                    return SegueName.BARN_DOOR_OPEN;
                } else if (direction == VisualEffectDirection.CLOSE) {
                    return SegueName.BARN_DOOR_CLOSE;
                }
                break;
            case CHECKERBOARD:
                return SegueName.CHECKERBOARD;
            case IRIS:
                if (direction == VisualEffectDirection.OPEN) {
                    return SegueName.IRIS_OPEN;
                } else if (direction == VisualEffectDirection.CLOSE) {
                    return SegueName.IRIS_CLOSE;
                }
                break;
            case PLAIN:
                return SegueName.PLAIN;
            case PUSH:
                // TODO: Push effect not implemented; delegate to scroll
                if (direction == VisualEffectDirection.UP) {
                    return SegueName.SCROLL_UP;
                } else if (direction == VisualEffectDirection.DOWN) {
                    return SegueName.SCROLL_DOWN;
                } else if (direction == VisualEffectDirection.LEFT) {
                    return SegueName.SCROLL_LEFT;
                } else if (direction == VisualEffectDirection.RIGHT) {
                    return SegueName.SCROLL_RIGHT;
                }
                break;
            case SCROLL:
                if (direction == VisualEffectDirection.UP) {
                    return SegueName.SCROLL_UP;
                } else if (direction == VisualEffectDirection.DOWN) {
                    return SegueName.SCROLL_DOWN;
                } else if (direction == VisualEffectDirection.LEFT) {
                    return SegueName.SCROLL_LEFT;
                } else if (direction == VisualEffectDirection.RIGHT) {
                    return SegueName.SCROLL_RIGHT;
                }
                break;
            case SHRINK:
                if (direction == VisualEffectDirection.TOP) {
                    return SegueName.SHRINK_TO_TOP;
                } else if (direction == VisualEffectDirection.CENTER) {
                    return SegueName.SHRINK_TO_CENTER;
                } else if (direction == VisualEffectDirection.BOTTOM) {
                    return SegueName.SHRINK_TO_BOTTOM;
                }
                break;
            case STRETCH:
                if (direction == VisualEffectDirection.TOP) {
                    return SegueName.STRETCH_FROM_TOP;
                } else if (direction == VisualEffectDirection.CENTER) {
                    return SegueName.STRETCH_FROM_CENTER;
                } else if (direction == VisualEffectDirection.BOTTOM) {
                    return SegueName.STRETCH_FROM_BOTTOM;
                }
                break;
            case VENETIAN_BLINDS:
                return SegueName.VENETIAN_BLINDS;
            case WIPE:
                if (direction == VisualEffectDirection.UP) {
                    return SegueName.WIPE_UP;
                } else if (direction == VisualEffectDirection.DOWN) {
                    return SegueName.WIPE_DOWN;
                } else if (direction == VisualEffectDirection.LEFT) {
                    return SegueName.WIPE_LEFT;
                } else if (direction == VisualEffectDirection.RIGHT) {
                    return SegueName.WIPE_RIGHT;
                }
                break;
            case ZOOM:
                if (direction == VisualEffectDirection.IN || direction == VisualEffectDirection.OPEN) {
                    return SegueName.ZOOM_IN;
                } else if (direction == VisualEffectDirection.OUT || direction == VisualEffectDirection.CLOSE) {
                    return SegueName.ZOOM_OUT;
                }
                break;
        }

        throw new HtSemanticException("Not a visual effect.");
    }
}
