package com.defano.hypercard.gui;

import javax.swing.*;

public abstract class HyperCardDialog implements HyperCardWindow<JDialog> {

    private JDialog dialog;

    @Override
    public JDialog getWindow() {
        return dialog;
    }

    @Override
    public void setWindow(JDialog windowFrame) {
        this.dialog = windowFrame;
    }
}
