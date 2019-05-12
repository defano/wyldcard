package com.defano.hypertalk.ast.statement;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.ListExp;
import com.defano.hypertalk.ast.expression.container.VariableExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.ast.statement.command.MessageCmd;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
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
            MessageCmd messageCmd = new MessageCmd(super.getParserContext(), expression.evaluate(context).toString(), new ListExp(null));
            messageCmd.execute(context);
        }

        Value v = expression.evaluate(context);
        context.setIt(v);
    }
}
