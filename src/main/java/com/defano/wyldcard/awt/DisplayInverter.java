package com.defano.wyldcard.awt;

import com.defano.jmonet.transform.image.ApplyPixelTransform;
import com.defano.jmonet.transform.pixel.InvertPixelTransform;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public interface DisplayInverter {

    default BufferedImage invertedPixels(Shape clip, JComponent component) {
        Container parent = component.getParent();

        BufferedImage buffer = new BufferedImage(parent.getWidth(), parent.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics();
        g2d.setClip(clip);
        component.setVisible(false);
        parent.printAll(g2d);
        component.setVisible(true);
        g2d.dispose();

        Rectangle bounds = clip.getBounds();

        BufferedImage invert = new ApplyPixelTransform(new InvertPixelTransform())
                .apply(buffer.getSubimage(
                        Math.max(0, bounds.x),
                        Math.max(0, bounds.y),
                        Math.min(buffer.getWidth() - bounds.x, bounds.width),
                        Math.min(buffer.getHeight() - bounds.y, bounds.height)));

        if (bounds.x < 0 || bounds.y < 0) {

            int dx = bounds.x < 0 ? -bounds.x : 0;
            int dy = bounds.y < 0 ? -bounds.y : 0;

            BufferedImage resized = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            g2d = resized.createGraphics();
            g2d.drawImage(invert, dx, dy, null);
            g2d.dispose();
            return resized;
        } else {
            return invert;
        }

    }

    default void printComponent(Graphics2D g2d, Container parent, Component component) {
        for (Component c : parent.getComponents()) {
            if (c != component) {
                    g2d.translate(c.getX(), c.getY());
                    c.printAll(g2d);
                    g2d.translate(-c.getX(), -c.getY());
            }
        }

    }

}
