package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.SelectionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

public class SelectionExp extends Expression {

    @Override
    public Value evaluate() throws HtSemanticException {
        return SelectionContext.getInstance().getSelection();
    }
}
