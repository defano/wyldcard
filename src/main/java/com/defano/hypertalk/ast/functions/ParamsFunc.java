package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;

public class ParamsFunc extends Expression {

    @Override
    public Value evaluate() throws HtSemanticException {
        List<Value> params = ExecutionContext.getContext().getParams();
        StringBuilder paramList = new StringBuilder();

        for (int index = 0; index < params.size(); index++) {
            paramList.append(params.get(index).stringValue());
            if (index != params.size() - 1) {
                paramList.append(", ");
            }
        }

        return new Value(paramList.toString());
    }
}
