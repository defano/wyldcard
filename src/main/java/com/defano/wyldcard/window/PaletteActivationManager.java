package com.defano.wyldcard.window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Assures that palettes generally follow the application window into the background when the user
 * foregrounds a different application on the native windowing system.
 */
public class PaletteActivationManager extends WindowAdapter {

    private final WyldCardFrame paletteWindow;

    public PaletteActivationManager(WyldCardFrame paletteWindow) {
        this.paletteWindow = paletteWindow;
    }

    @Override
    public void windowActivated(WindowEvent e) {
        paletteWindow.getWindow().setAlwaysOnTop(true);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (e.getOppositeWindow() == null) {
            paletteWindow.getWindow().setAlwaysOnTop(false);
            paletteWindow.getWindow().toBack();
        }
    }

}
