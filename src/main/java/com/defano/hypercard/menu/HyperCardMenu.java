package com.defano.hypercard.menu;

import javax.swing.*;

public class HyperCardMenu extends JMenu {

    public HyperCardMenu(String name) {
        super(name);
    }

    public void setVisible(boolean visible) {
        // Don't flicker the menubar when nothing is happening
        if (visible != super.isVisible()) {
            super.setVisible(visible);
            refreshMenuBar();
        }
    }

    public void refreshMenuBar() {
        if (getParent() != null) {
            JMenuBar menuBar = (JMenuBar) getParent();
            menuBar.updateUI();
        }
    }

}
