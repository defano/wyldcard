package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.runtime.context.ExecutionContext;
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
    public Value onEvaluate(ExecutionContext context) throws HtException {
        List<Value> params = context.getStackFrame().getParams();
        int evalParamNumber = theParamNumber.evaluate(context).integerValue();

        if (evalParamNumber == 0) {
            return new Value(context.getStackFrame().getMessage());
        } else if (params.size() < evalParamNumber) {
            return new Value();
        } else {
            return params.get(evalParamNumber - 1);
        }
    }
}
