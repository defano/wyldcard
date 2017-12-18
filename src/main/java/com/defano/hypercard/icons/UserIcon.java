package com.defano.hypercard.icons;

import javax.swing.*;

/**
 * A {@link ButtonIcon} backed by a user-generated image.
 */
public class UserIcon implements ButtonIcon {

    private final String name;
    private final byte[] imageData;

    public UserIcon(String name, byte[] imageData) {
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
    public AlphaImageIcon getImage() {
        return new AlphaImageIcon(new ImageIcon(imageData), 1.0f);
    }

}
