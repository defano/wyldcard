package com.defano.wyldcard.icons;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A {@link ButtonIcon} backed by a user-generated image.
 */
public class UserIcon implements ButtonIcon {

    private final String name;
    private final BufferedImage imageData;

    public UserIcon(String name, BufferedImage imageData) {
        this.name = name;
        this.imageData = imageData;
    }

    @Override
    public int getId() {
        return Math.abs(name.hashCode());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Image getImage() {
        return imageData;
    }

    @Override
    public AlphaImageIcon getIcon() {
        return new AlphaImageIcon(new ImageIcon(getImage()), 1.0f);
    }
}
