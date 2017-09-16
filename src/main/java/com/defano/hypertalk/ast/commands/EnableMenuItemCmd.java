package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.common.MenuItemSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;

public class EnableMenuItemCmd extends Command {

    private MenuItemSpecifier menuItem;
    private boolean enabled;

    public EnableMenuItemCmd(MenuItemSpecifier menuItem, boolean enabled) {
        super("enable");

        this.menuItem = menuItem;
        this.enabled = enabled;
    }


    @Override
    public void onExecute() throws HtException {
        JMenu theMenu = menuItem.getSpecifiedMenu();
        int menuItemIndex = menuItem.getSpecifiedItemIndex();

        if (menuItemIndex < 0 || menuItemIndex > theMenu.getItemCount()) {
            throw new HtSemanticException("No such menu item in menu " + theMenu.getText());
        }

        theMenu.getItem(menuItemIndex).setEnabled(enabled);
    }
}
