package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.commands.MessageCmd;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.VariableContainerExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExpressionStatement extends Statement {

    public final Expression expression;
    
    public ExpressionStatement(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }
    
    public void onExecute() throws HtException, Breakpoint {

        // Special case: A variable name used as a statement should be interpreted as a message command
        if (expression instanceof VariableContainerExp) {
            MessageCmd messageCmd = new MessageCmd(super.getContext(), expression.evaluate().stringValue(), new ExpressionList());
            messageCmd.execute();
        }

        Value v = expression.evaluate();
        ExecutionContext.getContext().setIt(v);
    }
}
