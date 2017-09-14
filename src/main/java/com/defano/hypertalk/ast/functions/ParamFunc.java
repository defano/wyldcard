package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;

public class ParamFunc extends Expression {

    private final Expression theParamNumber;

    public ParamFunc(Expression theParamNumber) {
        this.theParamNumber = theParamNumber;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
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
