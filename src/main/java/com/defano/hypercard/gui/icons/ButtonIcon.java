package com.defano.hypercard.gui.icons;

import javax.swing.*;

public class ButtonIcon {

    private final ImageIcon icon;
    private final int id;

    public ButtonIcon(ImageIcon icon, int id) {
        this.icon = icon;
        this.id = id;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public int getId() {
        return id;
    }

}
