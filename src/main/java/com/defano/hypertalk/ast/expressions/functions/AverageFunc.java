package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.ExpressionList;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class AverageFunc extends ArgListFunction {

    public AverageFunc(ParserRuleContext context, ExpressionList argumentList) {
        super(context, argumentList);
    }

    public AverageFunc(ParserRuleContext context, Expression expression) {
        super(context, expression);
    }

    public Value onEvaluate() throws HtException {
        float sum = 0;
        List<Value> list = evaluateArgumentList();

        if (list.size() == 0) {
            return new Value(0);
        }
        
        for (Value item : list) {

            if (!item.isNumber()) {
                throw new HtSemanticException("All arguments to average must be numbers.");
            }
                
            sum += item.doubleValue();
        }
        
        return new Value(sum/list.size());
    }
}
