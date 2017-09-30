package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.runtime.context.HyperCardProperties;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class SelectionFunc extends Expression {

    @Override
    public Value evaluate() throws HtSemanticException {
        Value selection = HyperCardProperties.getInstance().getKnownProperty(HyperCardProperties.PROP_SELECTEDTEXT);

        if (selection == null || selection.isEmpty()) {
            throw new HtSemanticException("This isn't any selection.");
        }

        return selection;
    }
}
