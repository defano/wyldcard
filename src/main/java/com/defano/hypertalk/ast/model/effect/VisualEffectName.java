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
                .orElseThrow(() -> new HtSemanticException("Not a visual effect."));
    }

    public Class<? extends AnimatedSegue> toAnimatedSegueClass(VisualEffectDirection direction) throws HtSemanticException {
        switch (this) {
            case DISSOLVE:
                return PixelDissolveEffect.class;
            case BARN_DOOR:
                return getBarnDoorEffect(direction);
            case CHECKERBOARD:
                return CheckerboardEffect.class;
            case IRIS:
                return getIrisEffect(direction);
            case PLAIN:
                return PlainEffect.class;
            case PUSH:
            case SCROLL:
                return getScrollEffect(direction);
            case SHRINK:
                return getShrinkEffect(direction);
            case STRETCH:
                return getStretchEffect(direction);
            case VENETIAN_BLINDS:
                return BlindsEffect.class;
            case WIPE:
                return getWipeEffect(direction);
            case ZOOM:
                return getZoomEffect(direction);
        }

        throw new HtSemanticException("Not a visual effect.");
    }

    private Class<? extends AnimatedSegue> getBarnDoorEffect(VisualEffectDirection direction) throws HtSemanticException {
        if (direction == VisualEffectDirection.OPEN) {
            return BarnDoorOpenEffect.class;
        } else if (direction == VisualEffectDirection.CLOSE) {
            return BarnDoorCloseEffect.class;
        }

        throw new HtSemanticException("Not a valid direction for barn door effect.");
    }

    private Class<? extends AnimatedSegue> getIrisEffect(VisualEffectDirection direction) throws HtSemanticException {
        if (direction == VisualEffectDirection.OPEN) {
            return IrisOpenEffect.class;
        } else if (direction == VisualEffectDirection.CLOSE) {
            return IrisCloseEffect.class;
        }

        throw new HtSemanticException("Not a valid direction for iris effect.");
    }

    private Class<? extends AnimatedSegue> getScrollEffect(VisualEffectDirection direction) throws HtSemanticException {
        if (direction == VisualEffectDirection.UP) {
            return ScrollUpEffect.class;
        } else if (direction == VisualEffectDirection.DOWN) {
            return ScrollDownEffect.class;
        } else if (direction == VisualEffectDirection.LEFT) {
            return ScrollLeftEffect.class;
        } else if (direction == VisualEffectDirection.RIGHT) {
            return ScrollRightEffect.class;
        }

        throw new HtSemanticException("Not a valid direction for scroll or push effect.");
    }

    private Class<? extends AnimatedSegue> getShrinkEffect(VisualEffectDirection direction) throws HtSemanticException {
        if (direction == VisualEffectDirection.TOP) {
            return ShrinkToTopEffect.class;
        } else if (direction == VisualEffectDirection.CENTER) {
            return ShrinkToCenterEffect.class;
        } else if (direction == VisualEffectDirection.BOTTOM) {
            return ShrinkToBottomEffect.class;
        }

        throw new HtSemanticException("Not a valid direction for shrink effect.");
    }

    private Class<? extends AnimatedSegue> getStretchEffect(VisualEffectDirection direction) throws HtSemanticException {
        if (direction == VisualEffectDirection.TOP) {
            return StretchFromTopEffect.class;
        } else if (direction == VisualEffectDirection.CENTER) {
            return StretchFromCenterEffect.class;
        } else if (direction == VisualEffectDirection.BOTTOM) {
            return StretchFromBottomEffect.class;
        }

        throw new HtSemanticException("Not a valid direction for stretch effect.");
    }

    private Class<? extends AnimatedSegue> getWipeEffect(VisualEffectDirection direction) throws HtSemanticException {
        if (direction == VisualEffectDirection.UP) {
            return WipeUpEffect.class;
        } else if (direction == VisualEffectDirection.DOWN) {
            return WipeDownEffect.class;
        } else if (direction == VisualEffectDirection.LEFT) {
            return WipeLeftEffect.class;
        } else if (direction == VisualEffectDirection.RIGHT) {
            return WipeRightEffect.class;
        }

        throw new HtSemanticException("Not a valid direction for wipe effect.");
    }

    private Class<? extends AnimatedSegue> getZoomEffect(VisualEffectDirection direction) throws HtSemanticException {
        if (direction == VisualEffectDirection.IN || direction == VisualEffectDirection.OPEN) {
            return ZoomInEffect.class;
        } else if (direction == VisualEffectDirection.OUT || direction == VisualEffectDirection.CLOSE) {
            return ZoomOutEffect.class;
        }

        throw new HtSemanticException("Not a valid direction for zoom effect.");
    }

}
