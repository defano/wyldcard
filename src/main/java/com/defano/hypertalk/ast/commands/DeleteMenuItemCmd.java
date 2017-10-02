package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;

public class DeleteMenuItemCmd extends Command {

    private MenuItemSpecifier menuItem;

    public DeleteMenuItemCmd(MenuItemSpecifier menuItem) {
        super("delete");
        this.menuItem = menuItem;
    }

    @Override
    public void onExecute() throws HtException {
        menuItem.getSpecifiedMenu().remove(menuItem.getSpecifiedItemIndex());
    }
}
