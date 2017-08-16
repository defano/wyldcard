/*
 * HyperCardWindow
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui;

import com.defano.jmonet.model.Provider;

import javax.swing.*;
import java.awt.event.*;

public abstract class HyperCardFrame extends JFrame implements HyperCardWindow<JFrame> {

    private final Provider<Boolean> windowVisibleProvider = new Provider<>(false);
    private boolean ownsMenubar = false;

    public HyperCardFrame() {
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

        // Swing does not allow a JMenuBar to "live" on multiple windows at once; this lets us "steal" the
        // menubar each time the window comes into focus.
        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                HyperCardFrame.this.applyMenuBar();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
            }
        });

    }

    @Override
    public JFrame getWindow() {
        return this;
    }

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
