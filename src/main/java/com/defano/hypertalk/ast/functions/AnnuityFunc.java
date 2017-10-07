package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class AnnuityFunc extends ArgListFunction {

    public AnnuityFunc(ParserRuleContext context, ExpressionList expressionList) {
        super(context, expressionList);
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        List<Value> evaluatedArgs = evaluateArgumentList();

        if (evaluatedArgs.size() != 2) {
            throw new HtSemanticException("Annuity function expects two arguments, but got " + evaluatedArgs.size());
        }

        double rate = evaluatedArgs.get(0).doubleValue();
        double periods = evaluatedArgs.get(1).doubleValue();

        return new Value((1 - Math.pow(1 + rate, periods * -1)) / rate);
    }
}
