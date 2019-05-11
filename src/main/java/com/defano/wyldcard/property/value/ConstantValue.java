package com.defano.wyldcard.property.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.property.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * A read-only value backed by a {@link Value} object.
 */
public class ConstantValue implements ConcreteValue {

    private final Value v;

    public ConstantValue(Value v) {
        this.v = v;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value get(ExecutionContext context, PropertiesModel model) {
        return v;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(ExecutionContext context, Value v, PropertiesModel model) throws HtException {
        throw new HtSemanticException("This property cannot be modified.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value rawValue() {
        return v;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ConstantValue{" +
                "v=" + v +
                '}';
    }
}
