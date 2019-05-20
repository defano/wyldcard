package com.defano.hypertalk.ast.model.effect;

import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.jsegue.AnimatedSegue;
import com.defano.jsegue.renderers.*;

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

    public Class<? extends AnimatedSegue> toAnimatedSegueClass(VisualEffectDirection direction) throws HtSemanticException {
        switch (this) {
            case DISSOLVE:
                return PixelDissolveEffect.class;
            case BARN_DOOR:
                if (direction == VisualEffectDirection.OPEN) {
                    return BarnDoorOpenEffect.class;
                } else if (direction == VisualEffectDirection.CLOSE) {
                    return BarnDoorCloseEffect.class;
                }
                break;
            case CHECKERBOARD:
                return CheckerboardEffect.class;
            case IRIS:
                if (direction == VisualEffectDirection.OPEN) {
                    return IrisOpenEffect.class;
                } else if (direction == VisualEffectDirection.CLOSE) {
                    return IrisCloseEffect.class;
                }
                break;
            case PLAIN:
                return PlainEffect.class;
            case PUSH:
                // TODO: Push effect not implemented; delegate to scroll
                if (direction == VisualEffectDirection.UP) {
                    return ScrollUpEffect.class;
                } else if (direction == VisualEffectDirection.DOWN) {
                    return ScrollDownEffect.class;
                } else if (direction == VisualEffectDirection.LEFT) {
                    return ScrollLeftEffect.class;
                } else if (direction == VisualEffectDirection.RIGHT) {
                    return ScrollRightEffect.class;
                }
                break;
            case SCROLL:
                if (direction == VisualEffectDirection.UP) {
                    return ScrollUpEffect.class;
                } else if (direction == VisualEffectDirection.DOWN) {
                    return ScrollDownEffect.class;
                } else if (direction == VisualEffectDirection.LEFT) {
                    return ScrollLeftEffect.class;
                } else if (direction == VisualEffectDirection.RIGHT) {
                    return ScrollRightEffect.class;
                }
                break;
            case SHRINK:
                if (direction == VisualEffectDirection.TOP) {
                    return ShrinkToTopEffect.class;
                } else if (direction == VisualEffectDirection.CENTER) {
                    return ShrinkToCenterEffect.class;
                } else if (direction == VisualEffectDirection.BOTTOM) {
                    return ShrinkToBottomEffect.class;
                }
                break;
            case STRETCH:
                if (direction == VisualEffectDirection.TOP) {
                    return StretchFromTopEffect.class;
                } else if (direction == VisualEffectDirection.CENTER) {
                    return StretchFromCenterEffect.class;
                } else if (direction == VisualEffectDirection.BOTTOM) {
                    return StretchFromBottomEffect.class;
                }
                break;
            case VENETIAN_BLINDS:
                return BlindsEffect.class;
            case WIPE:
                if (direction == VisualEffectDirection.UP) {
                    return WipeUpEffect.class;
                } else if (direction == VisualEffectDirection.DOWN) {
                    return WipeDownEffect.class;
                } else if (direction == VisualEffectDirection.LEFT) {
                    return WipeLeftEffect.class;
                } else if (direction == VisualEffectDirection.RIGHT) {
                    return WipeRightEffect.class;
                }
                break;
            case ZOOM:
                if (direction == VisualEffectDirection.IN || direction == VisualEffectDirection.OPEN) {
                    return ZoomInEffect.class;
                } else if (direction == VisualEffectDirection.OUT || direction == VisualEffectDirection.CLOSE) {
                    return ZoomOutEffect.class;
                }
                break;
        }

        throw new HtSemanticException("Not a visual effect.");
    }
}
