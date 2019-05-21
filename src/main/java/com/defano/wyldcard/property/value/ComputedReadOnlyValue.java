package com.defano.wyldcard.property.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.property.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.io.Serializable;

/**
 * A read-only value that invokes a {@link ComputedValueGetter} to fetch its synthesized value.
 */
public class ComputedReadOnlyValue implements PropertyValue, Serializable {

    private final transient ComputedValueGetter getter;

    public ComputedReadOnlyValue(ComputedValueGetter getter) {
        this.getter = getter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value get(ExecutionContext context, PropertiesModel model) {
        return getter.getComputedValue(context, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(ExecutionContext context, Value v, PropertiesModel model) throws HtSemanticException {
        throw new HtSemanticException("Cannot set this property.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ComputedReadOnlyValue{" +
                "getter=" + getter +
                '}';
    }
}
