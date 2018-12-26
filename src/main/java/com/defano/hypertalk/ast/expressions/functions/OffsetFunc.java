package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class OffsetFunc extends ArgListFunction {

    public OffsetFunc(ParserRuleContext context, Expression arguments) {
        super(context, arguments);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        List<Value> evaluatedArgs = evaluateArgumentList(context);

        if (evaluatedArgs.size() != 2) {
            throw new HtSemanticException("Offset function expects two arguments, but got " + evaluatedArgs.size());
        }

        String text1 = evaluatedArgs.get(0).toString();
        String text2 = evaluatedArgs.get(1).toString();

        return new Value(text2.indexOf(text1) + 1);
    }
}
