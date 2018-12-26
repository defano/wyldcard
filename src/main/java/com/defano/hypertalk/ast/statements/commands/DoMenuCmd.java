package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.menubar.main.HyperCardMenuBar;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class DoMenuCmd extends Statement {

    private final Expression theMenuItem;

    public DoMenuCmd(ParserRuleContext context, Expression theMenuItem) {
        super(context);
        this.theMenuItem = theMenuItem;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        HyperCardMenuBar.getInstance().doMenu(context, theMenuItem.evaluate(context).toString());
    }
}
