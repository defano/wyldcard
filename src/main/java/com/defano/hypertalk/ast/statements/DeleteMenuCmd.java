package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.common.MenuSpecifier;
import com.defano.hypertalk.exception.HtException;

public class DeleteMenuCmd extends Statement {

    private final MenuSpecifier menu;

    public DeleteMenuCmd(MenuSpecifier menu) {
        this.menu = menu;
    }

    @Override
    public void execute() throws HtException {
        HyperCardMenuBar.instance.remove(menu.getSpecifiedMenu());
    }
}
