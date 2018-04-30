package com.defano.hypertalk.ast.expressions.factor;

import com.defano.hypertalk.ast.expressions.Expression;

public class FactorAssociation<T extends Expression> {
    public Class<? extends Expression> expressionType;
    public FactorAction<T> action;

    public FactorAssociation(Class<T> expressionType, FactorAction<T> action) {
        this.expressionType = expressionType;
        this.action = action;
    }
}
