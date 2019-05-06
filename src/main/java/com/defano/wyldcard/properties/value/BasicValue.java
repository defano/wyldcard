package com.defano.wyldcard.properties.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * A read/write {@link PropertyValue} backed by a {@link Value} object.
 */
public class BasicValue implements ConcreteValue {

    private Value v;
    private transient ValueTransformer transformer;

    public BasicValue(Value v) {
        this.v = v;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value get(ExecutionContext context, PropertiesModel model) throws HtException {
        if (transformer != null) {
            return transformer.transform(context, model, v);
        } else {
            return v;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(ExecutionContext context, Value v, PropertiesModel model) throws HtException {
        this.v = v;
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
    public void applyOnGetTransform(ValueTransformer transform) {
        this.transformer = transform;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BasicValue{" +
                "v=" + v +
                ", transform=" + transformer +
                '}';
    }
}
