package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class GetCmd extends Command {

    public final Expression expression;
    public final PartSpecifier part;
    
    public GetCmd(ParserRuleContext context, Expression e) {
        super(context, "get");

        expression = e;
        part = null;
    }
    
    public void onExecute(ExecutionContext context) throws HtException {
        if (expression != null) {
            context.setIt(expression.evaluate(context));
        }
    }
}
