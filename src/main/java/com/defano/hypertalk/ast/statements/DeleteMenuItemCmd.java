package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.common.MenuItemSpecifier;
import com.defano.hypertalk.exception.HtException;

public class DeleteMenuItemCmd extends Statement {

    private MenuItemSpecifier menuItem;

    public DeleteMenuItemCmd(MenuItemSpecifier menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public void execute() throws HtException {
        menuItem.getSpecifiedMenu().remove(menuItem.getSpecifiedItemIndex());
    }
}
