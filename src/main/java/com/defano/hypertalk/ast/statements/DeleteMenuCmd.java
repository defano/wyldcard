package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.common.MenuSpecifier;
import com.defano.hypertalk.exception.HtException;

public class DeleteMenuCmd extends Command {

    private final MenuSpecifier menu;

    public DeleteMenuCmd(MenuSpecifier menu) {
        super("delete");
        this.menu = menu;
    }

    @Override
    public void onExecute() throws HtException {
        HyperCardMenuBar.instance.deleteMenu(menu.getSpecifiedMenu());
    }
}
