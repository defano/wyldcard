package com.defano.wyldcard.awt;

import com.defano.jmonet.transform.image.ApplyPixelTransform;
import com.defano.jmonet.transform.pixel.InvertPixelTransform;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * An interface that should be applied to a {@link Component} whose {@link Component#paint(Graphics)} method needs to
 * produce an inverted rendering of all the pixels appearing behind it.
 */
public interface DisplayInverter {

    /**
     * Creates an image in which every pixel drawn behind the given component is color-inverted.
     * <p>
     * In order to prevent paint cycles (an infinite loop of screen painting), the given component must implement this
     * interface as a marker. This component will not be included in the resultant rendering, only sibling components
     * in its parent.
     * <p>
     * The returned BufferedImage will always match the bounds of the given mask. When a portion of the component
     * appears outside the bounds of its parent component (i.e., "off screen"), the undefined pixels in the returned
     * BufferedImage are fully transparent.
     *
     * @param mask      Only pixels behind the component and also within the bounds of this shape will be inverted, all
     *                  other pixels will be left fully transparent.
     * @param component The component whose sibling components should
     * @return A BufferedImage whose size matches that of the given mask and
     */
    default BufferedImage invertDisplayedPixels(Shape mask, Component component) {

        if (!(component instanceof DisplayInverter)) {
            throw new IllegalArgumentException("Component must be marked with DisplayInverter interface to prevent a paint graph cycle.");
        }

        Container parent = component.getParent();

        // Create a list of all the components appearing behind this one
        List<Component> occlusions = new ArrayList<>();
        enumerateComponentsInDrawOrder(component.getParent().getComponents(), occlusions);

        // Create a buffer where we'll draw each of the components underneath us
        BufferedImage buffer = new BufferedImage(parent.getWidth(), parent.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Draw each component behind us into the buffer
        Graphics2D g2d = buffer.createGraphics();
        g2d.setClip(mask);
        for (Component c : occlusions) {
            // Be sure to skip us, or any other components that invert themselves (as doing so will produce an infinite draw loop)
            if (c != component && !(c instanceof DisplayInverter)) {
                Point loc = SwingUtilities.convertPoint(c.getParent(), c.getLocation(), parent);
                g2d.translate(loc.getX(), loc.getY());
                c.printAll(g2d);
                g2d.translate(-loc.getX(), -loc.getY());
            }
        }
        g2d.dispose();

        // All sibling components were rendered previously; trim buffer to bounds of component
        Rectangle maskBounds = mask.getBounds();
        BufferedImage subimage = buffer.getSubimage(
                Math.max(0, maskBounds.x),
                Math.max(0, maskBounds.y),
                Math.min(buffer.getWidth() - maskBounds.x, maskBounds.width),
                Math.min(buffer.getHeight() - maskBounds.y, maskBounds.height));

        // Invert the image
        BufferedImage invert = new ApplyPixelTransform(new InvertPixelTransform()).apply(subimage);

        // Simple case: Clipped image is fully within the bounds of the parent container, we're ready to go
        if (maskBounds.x > 0 && maskBounds.y >= 0) {
            return invert;
        }

        // Special case: Some portion of image is outside the bounds of the parent; render them transparent
        else {
            int dx = maskBounds.x < 0 ? -maskBounds.x : 0;
            int dy = maskBounds.y < 0 ? -maskBounds.y : 0;

            BufferedImage resized = new BufferedImage(maskBounds.width, maskBounds.height, BufferedImage.TYPE_INT_ARGB);
            g2d = resized.createGraphics();
            g2d.drawImage(invert, dx, dy, null);
            g2d.dispose();

            return resized;
        }
    }

    default void enumerateComponentsInDrawOrder(Component[] elements, List<Component> drawList) {
        for (Component c : elements) {
            drawList.add(0, c);
            if (c instanceof Container) {
                enumerateComponentsInDrawOrder(((Container) c).getComponents(), drawList);
            }
        }
    }

}
