package com.defano.hypercard.parts;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

public class ScreenCurtain extends JComponent {

    private BufferedImage curtainImage;

    public ScreenCurtain() {
        setVisible(false);
        setOpaque(true);
    }

    public void setCurtainImage(BufferedImage curtainImage) {
        this.curtainImage = curtainImage;
        setVisible(curtainImage != null);

        if (curtainImage != null) {
            this.setPreferredSize(new Dimension(curtainImage.getWidth(), curtainImage.getHeight()));
            this.invalidate();
        }

        if (SwingUtilities.isEventDispatchThread()) {
            this.repaint();
        } else {
            try {
                SwingUtilities.invokeAndWait(this::repaint);
            } catch (InterruptedException | InvocationTargetException e) {
                // Nothing to do
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (curtainImage == null) {
            super.paintComponent(g);
        } else {
            g.drawImage(curtainImage, 0, 0, null);
        }
    }

}
