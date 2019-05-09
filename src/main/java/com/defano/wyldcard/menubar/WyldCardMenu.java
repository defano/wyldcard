package com.defano.wyldcard.menubar;

import javax.swing.*;

/**
 * An extension of {@link JMenu} that prevents menubar flickering when adjusting visibility.
 */
public class WyldCardMenu extends JMenu {

    public WyldCardMenu(String name) {
        super(name);
    }

    public void setVisible(boolean visible) {
        // Don't flicker the menubar when nothing is happening
        if (visible != super.isVisible()) {
            super.setVisible(visible);
            refreshMenuBar();
        }
    }

    private void refreshMenuBar() {
        if (getParent() != null) {
            JMenuBar menuBar = (JMenuBar) getParent();
            menuBar.updateUI();
        }
    }

}
