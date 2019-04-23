package com.defano.wyldcard.icons;

import com.defano.jmonet.transform.image.ApplyPixelTransform;
import com.defano.jmonet.transform.pixel.InvertPixelTransform;

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

    default ImageIcon getPreviewIcon() {
        return new ImageIcon(scaleToIconSize(getImage()));
    }

    default ImageIcon getIcon() {
        return new ImageIcon(scaleToIconSize(getImage()));
    }

    default ImageIcon getInvertedIcon() {
        return new ImageIcon(new ApplyPixelTransform(new InvertPixelTransform()).apply(scaleToIconSize(getImage())));
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
