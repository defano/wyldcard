/*
 * WindowBuilder
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.window;

import com.defano.hypercard.gui.HyperCardDialog;
import com.defano.hypercard.gui.HyperCardFrame;
import com.defano.hypercard.gui.HyperCardWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WindowBuilder<T extends HyperCardWindow> {

    private final HyperCardWindow window;
    private Point location = null;
    private boolean initiallyVisible = true;
    private boolean resizable = false;
    private HyperCardFrame dock;
    private boolean isPalette = false;

    private WindowBuilder(HyperCardFrame window) {
        this.window = window;

        this.window.setContentPane(window.getWindowPanel());
        this.window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private WindowBuilder(HyperCardDialog window) {
        this.window = window;

        this.window.setContentPane(window.getWindowPanel());
        this.window.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public static WindowBuilder<HyperCardWindow> make(HyperCardFrame window) {
        return new WindowBuilder(window);
    }

    public static WindowBuilder<HyperCardDialog> make(HyperCardDialog window) {
        return new WindowBuilder<>(window);
    }

    public WindowBuilder withTitle(String title) {
        this.window.setTitle(title);
        return this;
    }

    public WindowBuilder resizeable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public WindowBuilder quitOnClose() {
        this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return this;
    }

    public WindowBuilder withModel(Object model) {
        window.bindModel(model);
        return this;
    }

    public WindowBuilder asPalette() {
        this.isPalette = true;
        return this;
    }

    public WindowBuilder dockTo(HyperCardFrame window) {
        this.dock = window;
        return this;
    }

    public WindowBuilder withLocationUnderneath(Component component) {
        this.window.getWindow().pack();

        int targetY = (int) component.getLocation().getY() + component.getHeight() + 10;
        int targetX = (int) component.getLocation().getX() - ((window.getWindow().getWidth() / 2) - (component.getWidth() / 2));
        location = new Point(targetX, targetY);

        return this;
    }

    public WindowBuilder withLocationLeftOf(Component component) {
        this.window.getWindow().pack();

        int targetY = (int) component.getLocation().getY();
        int targetX = (int) component.getLocation().getX() - window.getWindow().getWidth() - 10;
        location = new Point(targetX, targetY);

        return this;
    }

    public WindowBuilder withLocationCenteredOver(Component component) {
        this.window.getWindow().setLocationRelativeTo(component);
        return this;
    }

    public WindowBuilder notInitiallyVisible() {
        this.initiallyVisible = false;
        return this;
    }

    public WindowBuilder ownsMenubar() {
        this.window.setOwnsMenubar(true);
        return this;
    }

    public WindowBuilder asModal() {
        this.window.setIsModal();
        return this;
    }

    public HyperCardWindow build() {
        this.window.getWindow().pack();

        if (location == null) {
            this.window.getWindow().setLocationRelativeTo(null);
        } else {
            this.window.getWindow().setLocation(location);
        }


        this.window.applyMenuBar();

        if (window instanceof HyperCardDialog) {
            this.window.setAllowResizing(resizable);
        }

        window.getWindow().setVisible(initiallyVisible);

        // Very strange: When running inside IntelliJ on macOS, setResizable must be called after setVisible,
        // otherwise, the frame will "automagically" move to the lower left of the screen.
        // See: http://stackoverflow.com/questions/26332251/jframe-moves-to-the-bottom-left-corner-of-the-screen

        this.window.setAllowResizing(resizable);
        this.window.getWindow().setFocusableWindowState(!isPalette);

        if (dock != null) {
            dock.getWindow().addComponentListener(new ComponentAdapter() {
                private Point lastLocation;

                @Override
                public void componentMoved(ComponentEvent e) {
                    super.componentMoved(e);
                    Point location = e.getComponent().getLocation();

                    if (lastLocation != null) {
                        int deltaX = location.x - lastLocation.x;
                        int deltaY = location.y - lastLocation.y;

                        window.getWindow().setLocation(window.getWindow().getLocation().x + deltaX, window.getWindow().getLocation().y + deltaY);
                    }

                    lastLocation = location;
                }
            });
        }

        return window;
    }

}
