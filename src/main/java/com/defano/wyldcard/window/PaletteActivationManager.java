package com.defano.wyldcard.window;

import com.defano.wyldcard.WyldCard;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Assures that palettes follow the application window into the background when the user foregrounds a different
 * application on the native windowing system.
 */
public class PaletteActivationManager extends WindowAdapter {

    @Override
    public void windowActivated(WindowEvent e) {
        WyldCard.getInstance().getWindowManager().getPalettes(true).forEach(wyldCardFrame -> wyldCardFrame.getWindow().setAlwaysOnTop(true));
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        if (e.getOppositeWindow() == null) {
            WyldCard.getInstance().getWindowManager().getPalettes(true).forEach(wyldCardFrame -> {
                wyldCardFrame.getWindow().setAlwaysOnTop(false);
                wyldCardFrame.getWindow().toBack();
            });
        }
    }

}
