/*
 * HyperCardMenuItem
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 9:55 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import javax.swing.*;

public class HyperCardMenu extends JMenu {

    public HyperCardMenu(String name) {
        super(name);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        refreshMenuBar();
    }

    public void refreshMenuBar() {
        if (getParent() != null) {
            JMenuBar menuBar = (JMenuBar) getParent();
            menuBar.updateUI();
        }
    }

}
