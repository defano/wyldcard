package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class DoMenuCmd extends Command {

    private final Expression theMenuItem;

    public DoMenuCmd(ParserRuleContext context, Expression theMenuItem) {
        super(context, null);
        this.theMenuItem = theMenuItem;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        WyldCard.getInstance().getWyldCardMenuBar().doMenu(context, theMenuItem.evaluate(context).toString());
    }
}
