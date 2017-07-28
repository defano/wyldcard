package com.defano.hypercard.gui.fx;

import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.common.VisualEffectImage;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;
import com.defano.jsegue.AnimatedSegue;
import com.defano.jsegue.SegueName;
import com.defano.jsegue.renderers.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class VisualEffectFactory {

    public static AnimatedSegue create(VisualEffectSpecifier effectSpecifier, BufferedImage from, BufferedImage to) {
        AnimatedSegue effect = effectNamed(effectSpecifier.name);
        effect.setSource(from);
        effect.setDestination(effectImage(effectSpecifier.image, to));
        effect.setDurationMs(effectSpecifier.speed.durationMs);

        return effect;
    }

    public static PlainEffect createScreenLock() {
        BufferedImage screenShot = HyperCard.getInstance().getCard().getScreenshot();

        PlainEffect effect = new PlainEffect();
        effect.setSource(screenShot);
        effect.setDestination(screenShot);     // Ignored; never visible
        effect.setFps(1);
        effect.setDurationMs(Integer.MAX_VALUE);
        return effect;
    }

    private static AnimatedSegue effectNamed(SegueName name) {
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
            case ZOOM_IN:
                return new ZoomInEffect();
            case ZOOM_OUT:
                return new ZoomOutEffect();
            case PLAIN:
                return new PlainEffect();
            case STRETCH_FROM_TOP:
                return new StretchFromTopEffect();
            case STRETCH_FROM_BOTTOM:
                return new StretchFromBottomEffect();
            case STRETCH_FROM_CENTER:
                return new StretchFromCenterEffect();
            case SHRINK_TO_BOTTOM:
                return new ShrinkToBottomEffect();
            case SHRINK_TO_TOP:
                return new ShrinkToTopEffect();
            case SHRINK_TO_CENTER:
                return new ShrinkToCenterEffect();
            case VENETIAN_BLINDS:
                return new BlindsEffect();
            case CHECKERBOARD:
                return new CheckerboardEffect();

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
            case INVERSE:
                return invert(to);

            default:
                throw new IllegalArgumentException("Not implemented yet.");
        }
    }

    public static BufferedImage invert(BufferedImage image) {

        BufferedImage inverted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = inverted.createGraphics();
        g.drawImage(image,0, 0, null);
        g.dispose();

        for (int x = 0; x < inverted.getWidth(); x++) {
            for (int y = 0; y < inverted.getHeight(); y++) {

                int argb = inverted.getRGB(x, y);
                int alpha = 0xff000000 & argb;
                int rgb = 0x00ffffff & argb;

                // Invert preserving alpha channel
                inverted.setRGB(x, y, alpha | (~rgb & 0x00ffffff));
            }
        }

        return inverted;
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
