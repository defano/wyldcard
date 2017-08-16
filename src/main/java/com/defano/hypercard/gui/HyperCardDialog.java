package com.defano.hypercard.gui;

import com.defano.jmonet.model.Provider;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class HyperCardDialog extends JDialog implements HyperCardWindow<JDialog> {

    private final Provider<Boolean> windowVisibleProvider = new Provider<>(false);
    private boolean ownsMenubar;

    public HyperCardDialog() {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                windowVisibleProvider.set(true);
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                windowVisibleProvider.set(false);
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                windowVisibleProvider.set(false);
            }
        });
    }

    @Override
    public JDialog getWindow() {
        return this;
    }

    @Override
    public Provider<Boolean> getWindowVisibleProvider() {
        return windowVisibleProvider;
    }

    @Override
    public boolean ownsMenubar() {
        return this.ownsMenubar;
    }

    @Override
    public void setOwnsMenubar(boolean ownsMenubar) {
        this.ownsMenubar = ownsMenubar;
    }
}
