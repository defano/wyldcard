package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.Preemption;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.expressions.containers.VariableExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.commands.MessageCmd;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExpressionStatement extends Statement {

    public final Expression expression;
    
    public ExpressionStatement(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }
    
    public void onExecute(ExecutionContext context) throws HtException, Preemption {

        // Special case: A variable name used as a statement should be interpreted as a message command
        if (expression instanceof VariableExp) {
            MessageCmd messageCmd = new MessageCmd(super.getParserContext(), expression.evaluate(context).stringValue(), new ListExp(null));
            messageCmd.execute(context);
        }

        Value v = expression.evaluate(context);
        context.setIt(v);
    }
}
