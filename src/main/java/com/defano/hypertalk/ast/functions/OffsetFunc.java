package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;

public class OffsetFunc extends ArgListFunction {

    public OffsetFunc(ExpressionList arguments) {
        super(arguments);
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        List<Value> evaluatedArgs = evaluateArgumentList();

        if (evaluatedArgs.size() != 2) {
            throw new HtSemanticException("Offset function expects two arguments, but got " + evaluatedArgs.size());
        }

        String text1 = evaluatedArgs.get(0).stringValue();
        String text2 = evaluatedArgs.get(1).stringValue();

        return new Value(text2.indexOf(text1) + 1);
    }
}
