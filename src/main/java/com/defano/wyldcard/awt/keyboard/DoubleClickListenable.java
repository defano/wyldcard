package com.defano.wyldcard.awt.keyboard;

import com.defano.wyldcard.awt.mouse.MouseListenable;

import java.awt.event.*;

/**
 * A utility mix-in for objects wishing to be notified of double-click MouseEvents.
 */
public interface DoubleClickListenable extends MouseListenable {

    /**
     * Called to indicate that the mouse was double-clicked.
     * @param e The corresponding MouseEvent.
     */
    void onDoubleClick(MouseEvent e);

    @Override
    default void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            onDoubleClick(e);
        }
    }
}
