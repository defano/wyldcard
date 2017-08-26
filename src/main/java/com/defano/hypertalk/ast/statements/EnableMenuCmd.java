package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.common.MenuSpecifier;
import com.defano.hypertalk.exception.HtException;

import javax.swing.*;

public class EnableMenuCmd extends Statement {

    private final MenuSpecifier menu;
    private final boolean enabled;

    public EnableMenuCmd(MenuSpecifier menu, boolean enabled) {
        this.menu = menu;
        this.enabled = enabled;
    }

    @Override
    public void execute() throws HtException {
        JMenu theMenu = menu.getSpecifiedMenu();
        theMenu.setEnabled(enabled);
    }
}
