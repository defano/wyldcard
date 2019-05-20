package com.defano.wyldcard.effect;

import com.defano.hypertalk.ast.model.effect.VisualEffectImage;
import com.defano.hypertalk.ast.model.specifier.VisualEffectSpecifier;
import com.defano.jmonet.transform.image.ApplyPixelTransform;
import com.defano.jmonet.transform.pixel.InvertPixelTransform;
import com.defano.jsegue.AnimatedSegue;
import com.defano.jsegue.renderers.PlainEffect;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A factory of HyperCard visual effects.
 */
public class VisualEffectFactory {

    public static AnimatedSegue create(VisualEffectSpecifier effectSpecifier, BufferedImage from, BufferedImage to) {
        try {
            AnimatedSegue effect = effectSpecifier.effect.newInstance();
            effect.setSource(from);
            effect.setDestination(effectImage(effectSpecifier.image, to));
            effect.setDurationMs(effectSpecifier.speed.durationMs);

            return effect;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException("Bug! Visual effect renderer class cannot be instantiated.", e);
        }
    }

    public static PlainEffect createScreenLock(ExecutionContext context) {
        BufferedImage screenShot = context.getCurrentStack().getDisplayedCard().getScreenshot();

        PlainEffect effect = new PlainEffect();
        effect.setSource(screenShot);
        effect.setDestination(screenShot);     // Ignored; never visible
        effect.setFps(1);
        effect.setDurationMs(Integer.MAX_VALUE);
        return effect;
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
                return new ApplyPixelTransform(new InvertPixelTransform()).apply(to);
            default:
                throw new IllegalArgumentException("Bug! Not implemented: " + image);
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
