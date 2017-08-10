package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.common.MenuItemSpecifier;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;

public class EnableMenuItemCmd extends Statement {

    private MenuItemSpecifier menuItem;
    private boolean enabled;

    public EnableMenuItemCmd(MenuItemSpecifier menuItem, boolean enabled) {
        this.menuItem = menuItem;
        this.enabled = enabled;
    }


    @Override
    public void execute() throws HtException {
        JMenu theMenu = menuItem.getSpecifiedMenu();
        int menuItemIndex = menuItem.getSpecifiedItemIndex();

        if (menuItemIndex < 0 || menuItemIndex > theMenu.getItemCount()) {
            throw new HtSemanticException("No such menu item in menu " + theMenu.getText());
        }

        theMenu.getItem(menuItemIndex).setEnabled(enabled);
    }
}
