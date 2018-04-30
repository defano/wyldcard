package com.defano.wyldcard.window;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WindowDock extends ComponentAdapter {

    private static final WindowDock instance = new WindowDock();

    private Point lastLocation;
    private WyldCardFrame dock;
    private Set<WyldCardFrame> dockedWindows = new HashSet<>();

    private WindowDock() {
    }

    public static WindowDock getInstance() {
        return instance;
    }

    public void setDock(WyldCardFrame dock) {
        if (dock != null) {
            if (this.dock != null) {
                this.dock.getWindow().removeComponentListener(this);
            }

            this.dock = dock;
            this.dock.getWindow().addComponentListener(this);
            this.lastLocation = dock.getWindow().getLocation();
        }
    }

    public void dockWindows(Collection<WyldCardFrame> dockedWindows) {
        this.dockedWindows.addAll(dockedWindows);
    }

    public void undockWindows(Collection<WyldCardFrame> dockedWindows) {
        this.dockedWindows.removeAll(dockedWindows);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        Point location = e.getComponent().getLocation();

        if (lastLocation != null) {
            int deltaX = location.x - lastLocation.x;
            int deltaY = location.y - lastLocation.y;

            for (WyldCardFrame dockedWindow : dockedWindows) {
                dockedWindow.getWindow().setLocation(dockedWindow.getWindow().getLocation().x + deltaX, dockedWindow.getWindow().getLocation().y + deltaY);
            }
        }

        lastLocation = location;
    }

}
