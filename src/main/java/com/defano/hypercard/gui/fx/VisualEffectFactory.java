package com.defano.hypercard.gui.fx;

import com.defano.hypercard.gui.fx.renderers.DissolveEffect;
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

    private static AnimatedVisualEffect effectNamed(VisualEffectName name) {
        switch (name) {
            case DISSOLVE:
                return new DissolveEffect();
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
