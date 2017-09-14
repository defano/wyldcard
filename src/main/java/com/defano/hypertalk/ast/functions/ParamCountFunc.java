package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class ParamCountFunc extends Expression {

    @Override
    public Value evaluate() throws HtSemanticException {
        try {
            return new Value(ExecutionContext.getContext().getParams().size());

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return new Value();
    }
}
