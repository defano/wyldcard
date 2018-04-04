package com.defano.hypertalk.ast.statements.conditional;

import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Statement;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class IfStatement extends Statement {

    public final Expression condition;
    public final ThenElseBlock then;
    
    public IfStatement(ParserRuleContext context, Expression condition, ThenElseBlock then) {
        super(context);
        this.condition = condition;
        this.then = then;
    }
    
    public void onExecute(ExecutionContext context) throws HtException, Breakpoint {
        if (condition.evaluate(context).checkedBooleanValue()) {
            then.thenBranch.execute(context);
        } else if (then.elseBranch != null) {
            then.elseBranch.execute(context);
        }
    }
}
