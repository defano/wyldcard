package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class AnnuityFunc extends ArgListFunction {

    public AnnuityFunc(ParserRuleContext context, Expression expressionList) {
        super(context, expressionList);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        List<Value> evaluatedArgs = evaluateArgumentList(context);

        if (evaluatedArgs.size() != 2) {
            throw new HtSemanticException("Annuity function expects two arguments, but got " + evaluatedArgs.size());
        }

        double rate = evaluatedArgs.get(0).doubleValueOrError(new HtSemanticException("Expected a numerical value for annuity rate."));
        double periods = evaluatedArgs.get(1).doubleValueOrError(new HtSemanticException("Expected a numerical value for annuity period."));

        return new Value((1 - Math.pow(1 + rate, periods * -1)) / rate);
    }
}
