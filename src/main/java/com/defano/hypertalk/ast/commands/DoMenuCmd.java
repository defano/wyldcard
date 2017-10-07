package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DoMenuCmd extends Statement {

    private final Expression theMenuItem;

    public DoMenuCmd(ParserRuleContext context, Expression theMenuItem) {
        super(context);
        this.theMenuItem = theMenuItem;
    }

    @Override
    public void onExecute() throws HtException {
        HyperCardMenuBar.instance.doMenu(theMenuItem.evaluate().stringValue());
    }
}
