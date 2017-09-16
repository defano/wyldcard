package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;

public class DoMenuCmd extends Statement {

    private final Expression theMenuItem;

    public DoMenuCmd(Expression theMenuItem) {
        this.theMenuItem = theMenuItem;
    }

    @Override
    public void execute() throws HtException {
        HyperCardMenuBar.instance.doMenu(theMenuItem.evaluate().stringValue());
    }
}
