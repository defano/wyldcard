package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.exception.HtException;

public interface FactorAction<T> {
    void accept(T object) throws HtException;
}
