package com.defano.wyldcard.icons;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An icon that can be applied to a HyperCard button.
 */
public interface ButtonIcon {

    int getId();
    String getName();
    AlphaImageIcon getImage();

    default AlphaImageIcon getIcon() {
        return new AlphaImageIcon(new ImageIcon(scaleToIconSize(getImage().getImage())), 1.0f);
    }

    static int getIconSize() {
        return 32;
    }

    static BufferedImage scaleToIconSize(Image inputImage) {
        BufferedImage outputImage = new BufferedImage(getIconSize(), getIconSize(), BufferedImage.TYPE_INT_ARGB);

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, getIconSize(), getIconSize(), null);
        g2d.dispose();

        return outputImage;
    }

}
