package com.defano.hypercard.awt;

import java.awt.event.*;

public interface DoubleClickListenable extends MouseListenable {

    void onDoubleClick(MouseEvent e);

    @Override
    default void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            onDoubleClick(e);
        }
    }
}
