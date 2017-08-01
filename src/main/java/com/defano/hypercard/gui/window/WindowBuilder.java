/*
 * WindowBuilder
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.window;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.gui.HyperCardFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WindowBuilder {

    private final JFrame frame;
    private final HyperCardFrame window;
    private Point location = null;
    private boolean initiallyVisible = true;
    private boolean resizable = false;
    private HyperCardFrame dock;
    private boolean hasLocalMenubar = false;

    private WindowBuilder(HyperCardFrame window) {
        this.window = window;
        this.frame = new JFrame();

        frame.setContentPane(window.getWindowPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static WindowBuilder make(HyperCardFrame window) {
        return new WindowBuilder(window);
    }

    public WindowBuilder withTitle(String title) {
        frame.setTitle(title);
        return this;
    }

    public WindowBuilder hasLocalMenubar(boolean hasLocalMenubar) {
        this.hasLocalMenubar = hasLocalMenubar;
        return this;
    }

    public WindowBuilder resizeable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public WindowBuilder quitOnClose() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return this;
    }

    public WindowBuilder withModel(Object model) {
        window.bindModel(model);
        return this;
    }

    public WindowBuilder asPalette() {
        frame.setAlwaysOnTop(true);
        frame.setAutoRequestFocus(true);
        frame.setFocusableWindowState(false);

        return this;
    }

    public WindowBuilder dockTo(HyperCardFrame window) {
        this.dock = window;
        return this;
    }

    public WindowBuilder withMenuBar(JMenuBar menuBar) {

        // Swing does not allow a JMenuBar to "live" on multiple windows at once; this lets us "steal" the
        // menubar each time the window comes into focus.
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (HyperCard.getInstance().isMacOs() || hasLocalMenubar) {
                    frame.setJMenuBar(menuBar);
                }
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
            }
        });

        return this;
    }

    public WindowBuilder withLocationUnderneath(Component component) {
        frame.pack();

        int targetY = (int) component.getLocation().getY() + component.getHeight() + 10;
        int targetX = (int) component.getLocation().getX() - ((frame.getWidth() / 2) - (component.getWidth() / 2));
        location = new Point(targetX, targetY);

        return this;
    }

    public WindowBuilder withLocationLeftOf(Component component) {
        frame.pack();

        int targetY = (int) component.getLocation().getY();
        int targetX = (int) component.getLocation().getX() - frame.getWidth() - 10;
        location = new Point(targetX, targetY);

        return this;
    }

    public WindowBuilder withLocationCenteredOver(Component component) {
        frame.setLocationRelativeTo(component);
        return this;
    }

    public WindowBuilder notInitiallyVisible() {
        this.initiallyVisible = false;
        return this;
    }

    public JFrame build() {
        frame.pack();

        if (location == null) {
            frame.setLocationRelativeTo(null);
        } else {
            frame.setLocation(location);
        }

        window.setWindow(frame);
        window.setShown(initiallyVisible);

        // Very strange: When running inside IntelliJ on macOS, setResizable must be called after setVisible,
        // otherwise, the frame will "automagically" move to the lower left of the screen.
        // See: http://stackoverflow.com/questions/26332251/jframe-moves-to-the-bottom-left-corner-of-the-screen

        frame.setResizable(resizable);

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

                        frame.setLocation(frame.getLocation().x + deltaX, frame.getLocation().y + deltaY);
                    }

                    lastLocation = location;
                }
            });
        }

        return frame;
    }

}
