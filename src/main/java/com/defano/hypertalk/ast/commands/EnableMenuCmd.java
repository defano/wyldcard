package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.common.MenuSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;

import javax.swing.*;

public class EnableMenuCmd extends Command {

    private final MenuSpecifier menu;
    private final boolean enabled;

    public EnableMenuCmd(MenuSpecifier menu, boolean enabled) {
        super("enable");

        this.menu = menu;
        this.enabled = enabled;
    }

    @Override
    public void onExecute() throws HtException {
        JMenu theMenu = menu.getSpecifiedMenu();
        theMenu.setEnabled(enabled);
    }
}
