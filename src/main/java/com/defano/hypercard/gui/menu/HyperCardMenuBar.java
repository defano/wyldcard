/*
 * HyperCardMenuBar
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HyperCardMenuBar extends JMenuBar {

    public final static HyperCardMenuBar instance = new HyperCardMenuBar();

    private HyperCardMenuBar() {
        reset();
    }

    public void reset() {
        // Reset menu items in each menu
        FileMenu.instance.reset();
        EditMenu.instance.reset();
        GoMenu.instance.reset();
        ToolsMenu.instance.reset();
        PaintMenu.instance.reset();
        OptionsMenu.instance.reset();
        ObjectsMenu.instance.reset();
        FontMenu.instance.reset();
        StyleMenu.instance.reset();

        // Reset menus in the menu bar
        removeAll();
        add(FileMenu.instance);
        add(EditMenu.instance);
        add(GoMenu.instance);
        add(ToolsMenu.instance);
        add(PaintMenu.instance);
        add(OptionsMenu.instance);
        add(ObjectsMenu.instance);
        add(FontMenu.instance);
        add(StyleMenu.instance);
    }

    public void doMenu(String theMenuItem) throws HtSemanticException {
        JMenuItem foundMenuItem = findMenuItemByName(theMenuItem);
        if (foundMenuItem != null) {
            for (ActionListener thisAction : foundMenuItem.getActionListeners()) {
                thisAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "doMenu"));
            }

            return;
        }

        throw new HtSemanticException("Can't find menu item " + theMenuItem);
    }

    public void createMenu(String name) throws HtSemanticException {
        if (findMenuByName(name) != null) {
            throw new HtSemanticException("A menu named " + name + " already exists.");
        }

        add(new HyperCardMenu(name));
    }

    public JMenu findMenuByNumber(int index) throws HtSemanticException {
        if (index < 0 || index >= getMenuCount()) {
            return null;
        }

        return getMenu(index);
    }

    public JMenu findMenuByName(String name) {
        for (int thisMenuIndex = 0; thisMenuIndex < this.getMenuCount(); thisMenuIndex++) {
            JMenu thisMenu = this.getMenu(thisMenuIndex);

            if (thisMenu != null && name.equalsIgnoreCase(thisMenu.getText())) {
                return thisMenu;
            }
        }

        return null;
    }

    private JMenuItem findMenuItemByName(String name) {
        for (int thisMenuIndex = 0; thisMenuIndex < this.getMenuCount(); thisMenuIndex++) {
            JMenu thisMenu = this.getMenu(thisMenuIndex);

            for (int thisMenuItemIndex = 0; thisMenuItemIndex < thisMenu.getItemCount(); thisMenuItemIndex++) {
                JMenuItem thisMenuItem = thisMenu.getItem(thisMenuItemIndex);

                if (thisMenuItem != null && thisMenuItem.getText() != null && thisMenuItem.getText().equalsIgnoreCase(name)) {
                    return thisMenuItem;
                }
            }
        }

        return null;
    }

}
