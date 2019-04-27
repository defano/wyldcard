package com.defano.wyldcard.window;

import com.defano.wyldcard.WyldCard;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowActivationManager extends WindowAdapter {

    @Override
    public void windowClosed(WindowEvent e) {
        WyldCard.getInstance().getWindowManager().notifyWindowVisibilityChanged();
    }

    @Override
    public void windowOpened(WindowEvent e) {
        WyldCard.getInstance().getWindowManager().notifyWindowVisibilityChanged();
    }
}
