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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class HyperCardFrame extends WindowAdapter implements HyperCardWindow<JFrame> {

    // The Swing frame that this window is displayed in; bound only after the window has been built via WindowBuilder
    private JFrame windowFrame;
    private boolean isShown = false;
    private boolean ownsMenubar = false;

    private final Provider<Boolean> windowVisibleProvider = new Provider<>(false);

    @Override
    public JFrame getWindow() {
        return windowFrame;
    }

    @Override
    public void setWindow(JFrame windowFrame) {
        this.windowFrame = windowFrame;
        this.windowFrame.addWindowListener(this);
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
        windowVisibleProvider.set(shown);

        setVisible(shown);
    }

    public boolean isVisible() {
        return windowFrame != null && windowFrame.isVisible();
    }

    public void setVisible(boolean physicallyVisible) {
        if (windowFrame != null) {
            windowFrame.setVisible(physicallyVisible);
        }
    }

    public void toggleVisible() {
        setShown(!isShown());
    }

    public void dispose() {
        SwingUtilities.getWindowAncestor(getWindowPanel()).dispose();
        windowVisibleProvider.set(false);
    }

    public Provider<Boolean> getWindowVisibleProvider() {
        return windowVisibleProvider;
    }

    public void windowClosed(WindowEvent e) {
        windowVisibleProvider.set(windowFrame.isVisible());
    }

    public boolean ownsMenubar() {
        return ownsMenubar;
    }

    public void setOwnsMenubar(boolean ownsMenubar) {
        this.ownsMenubar = ownsMenubar;
    }
}
