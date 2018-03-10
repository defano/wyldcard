package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.statements.Command;
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
    
    public void onExecute () throws HtException {
        if (expression != null) {
            ExecutionContext.getContext().setIt(expression.evaluate());
        }
    }
}
