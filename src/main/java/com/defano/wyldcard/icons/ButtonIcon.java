package com.defano.wyldcard.icons;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An icon that can be applied to a HyperCard button.
 */
public interface ButtonIcon {

    int ICON_WIDTH = 32;
    int ICON_HEIGHT = 32;

    int getId();
    String getName();
    Image getImage();

    default AlphaImageIcon getPreviewIcon() {
        return new AlphaImageIcon(new ImageIcon(scaleToIconSize(getImage())), 1.0f);
    }

    default AlphaImageIcon getIcon() {
        return new AlphaImageIcon(new ImageIcon(scaleToIconSize(getImage())), 1.0f);
    }

    static BufferedImage scaleToIconSize(Image inputImage) {
        BufferedImage outputImage = new BufferedImage(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, ICON_WIDTH, ICON_HEIGHT, null);
        g2d.dispose();

        return outputImage;
    }

}
