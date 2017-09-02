/*
 * DoubleClickListener
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.util;

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
