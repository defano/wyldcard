package com.defano.hypercard.parts.stack;

import com.defano.hypercard.parts.card.CardPart;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A Swing component used to obscure the "actual" contents of the stack window. Used for screen locking and card-to-card
 * animated visual effects.
 */
public class ScreenCurtain extends JLabel {

    public ScreenCurtain() {
        setVisible(false);
        setOpaque(true);
    }

    /**
     * Sets the image to be displayed atop the stack window. Typically a card screen shot ({@link CardPart#getScreenshot()}
     * or a frame in a visual effect animation.
     *
     * When null, the curtain "opens" and reveals the contents the behind it. (Sets the visible property of this
     * component to false).
     *
     * @param curtainImage The image to drape over the stack window; null to open the curtain and reveal the card
     *                     underneath.
     */
    public void setCurtainImage(BufferedImage curtainImage) {
        setVisible(curtainImage != null);

        if (curtainImage != null) {
            this.setIcon(new ImageIcon(curtainImage));
            this.setPreferredSize(new Dimension(curtainImage.getWidth(), curtainImage.getHeight()));
            this.invalidate();
        }
    }
}
