package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DeleteMenuItemCmd extends Command {

    private MenuItemSpecifier menuItem;

    public DeleteMenuItemCmd(ParserRuleContext context, MenuItemSpecifier menuItem) {
        super(context, "delete");
        this.menuItem = menuItem;
    }

    @Override
    public void onExecute() throws HtException {
        menuItem.getSpecifiedMenu().remove(menuItem.getSpecifiedItemIndex());
    }
}
