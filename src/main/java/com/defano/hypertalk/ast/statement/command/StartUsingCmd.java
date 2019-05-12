package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class StartUsingCmd extends Command {

    private final Expression stackExpr;

    public StartUsingCmd(ParserRuleContext context, Expression stackExpr) {
        super(context, "start");
        this.stackExpr = stackExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        //TODO: Implement
        System.err.println("Start using command is not implemented yet.");
    }
}
