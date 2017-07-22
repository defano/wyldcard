package com.defano.hypercard.gui.fx;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.gui.fx.renderers.*;
import com.defano.hypertalk.ast.common.VisualEffectImage;
import com.defano.hypertalk.ast.common.VisualEffectName;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;

import java.awt.*;
import java.awt.image.BufferedImage;

public class VisualEffectFactory {

    public static AnimatedVisualEffect create(VisualEffectSpecifier effectSpecifier, BufferedImage from, BufferedImage to) {
        AnimatedVisualEffect effect = effectNamed(effectSpecifier.name);
        effect.setFrom(from);
        effect.setTo(effectImage(effectSpecifier.image, to));
        effect.setDurationMs(effectSpecifier.speed.durationMs);

        return effect;
    }

    public static FreezeEffect createScreenLock() {
        FreezeEffect effect = new FreezeEffect();
        effect.setFrom(HyperCard.getInstance().getCard().getScreenshot());
        effect.setFps(1);
        effect.setDurationMs(Integer.MAX_VALUE);
        return effect;
    }

    private static AnimatedVisualEffect effectNamed(VisualEffectName name) {
        switch (name) {
            case DISSOLVE:
                return new DissolveEffect();
            case SCROLL_LEFT:
                return new ScrollLeftEffect();
            case SCROLL_RIGHT:
                return new ScrollRightEffect();
            case SCROLL_UP:
                return new ScrollUpEffect();
            case SCROLL_DOWN:
                return new ScrollDownEffect();
            case BARN_DOOR_OPEN:
                return new BarnDoorOpenEffect();
            case BARN_DOOR_CLOSE:
                return new BarnDoorCloseEffect();
            case WIPE_LEFT:
                return new WipeLeftEffect();
            case WIPE_RIGHT:
                return new WipeRightEffect();
            case WIPE_UP:
                return new WipeUpEffect();
            case WIPE_DOWN:
                return new WipeDownEffect();
            case IRIS_OPEN:
                return new IrisOpenEffect();
            case IRIS_CLOSE:
                return new IrisCloseEffect();
            case ZOOM_CLOSE:
                return new ZoomCloseEffect();
            case ZOOM_OPEN:
                return new ZoomOpenEffect();

            default:
                throw new IllegalArgumentException("Unimplemented visual effect: " + name);
        }
    }

    private static BufferedImage effectImage(VisualEffectImage image, BufferedImage to) {
        switch (image) {
            case BLACK:
                return solidColorImage(to.getWidth(), to.getHeight(), Color.BLACK);
            case GRAY:
                return solidColorImage(to.getWidth(), to.getHeight(), Color.GRAY);
            case CARD:
                return to;
            case WHITE:
                return solidColorImage(to.getWidth(), to.getHeight(), Color.WHITE);

            default:
                throw new IllegalArgumentException("Not implemented yet.");
        }
    }

    private static BufferedImage solidColorImage(int width, int height, Color color) {
        BufferedImage solid = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = solid.createGraphics();
        g.setPaint(color);
        g.fillRect(0, 0, width, height);
        g.dispose();
        return solid;
    }

}
