package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class CompoundFunc extends ArgListFunction {

    public CompoundFunc(ParserRuleContext context, Expression argumentList) {
        super(context, argumentList);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        List<Value> evaluatedArgs = evaluateArgumentList(context);

        if (evaluatedArgs.size() != 2 || !evaluatedArgs.get(0).isNumber() || !evaluatedArgs.get(1).isNumber()) {
            throw new HtSemanticException("Compound function expects two numeric arguments, but got " + evaluatedArgs.size());
        }

        double rate = evaluatedArgs.get(0).doubleValue();
        double periods = evaluatedArgs.get(1).doubleValue();

        return new Value(Math.pow(1 + rate, periods));
    }

}
