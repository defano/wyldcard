package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class CompoundFunc extends ArgListFunction {

    public CompoundFunc(ParserRuleContext context, ExpressionList argumentList) {
        super(context, argumentList);
    }

    @Override
    public Value onEvaluate() throws HtException {
        List<Value> evaluatedArgs = evaluateArgumentList();

        if (evaluatedArgs.size() != 2) {
            throw new HtSemanticException("Compound function expects two arguments, but got " + evaluatedArgs.size());
        }

        double rate = evaluatedArgs.get(0).doubleValue();
        double periods = evaluatedArgs.get(1).doubleValue();

        return new Value(Math.pow(1 + rate, periods));
    }

}
