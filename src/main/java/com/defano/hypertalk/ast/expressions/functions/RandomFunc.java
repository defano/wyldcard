package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Random;

public class RandomFunc extends ArgListFunction {

    public RandomFunc(ParserRuleContext context, Expression bound) {
        super(context, bound);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {

        // Get the single argument passed to the function
        Value evaluatedArgument = super.evaluateSingleArgumentList(context);

        // Argument must be a natural (whole, non-negative, non-zero) number
        if (evaluatedArgument.isNatural()) {
            return new Value(new Random().nextInt(evaluatedArgument.integerValue()));
        } else {
            throw new HtSemanticException("Random expects a non-negative integer, but got '" + evaluatedArgument.stringValue() + "'");
        }
    }
}
