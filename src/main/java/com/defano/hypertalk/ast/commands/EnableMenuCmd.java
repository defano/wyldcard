package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.specifiers.MenuSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;

public class EnableMenuCmd extends Command {

    private final MenuSpecifier menu;
    private final boolean enabled;

    public EnableMenuCmd(ParserRuleContext context, MenuSpecifier menu, boolean enabled) {
        super(context, "enable");

        this.menu = menu;
        this.enabled = enabled;
    }

    @Override
    public void onExecute() throws HtException {
        JMenu theMenu = menu.getSpecifiedMenu();
        theMenu.setEnabled(enabled);
    }
}
