package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class ParamFunc extends Expression {

    private final Expression theParamNumber;

    public ParamFunc(ParserRuleContext context, Expression theParamNumber) {
        super(context);
        this.theParamNumber = theParamNumber;
    }

    @Override
    public Value onEvaluate() throws HtException {
        List<Value> params = ExecutionContext.getContext().getParams();
        int evalParamNumber = theParamNumber.evaluate().integerValue();

        if (evalParamNumber == 0) {
            return new Value(ExecutionContext.getContext().getMessage());
        } else if (params.size() < evalParamNumber) {
            return new Value();
        } else {
            return params.get(evalParamNumber - 1);
        }
    }
}
