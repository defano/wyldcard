/*
 * DoubleClickListener
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.util;

import java.awt.event.*;

public interface DoubleClickListener extends MouseListener {

    void onDoubleClick(MouseEvent e);

    @Override
    default void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            onDoubleClick(e);
        }
    }

    default void mousePressed(MouseEvent e) {}
    default void mouseReleased(MouseEvent e) {}
    default void mouseEntered(MouseEvent e) {}
    default void mouseExited(MouseEvent e) {}
}
