package com.defano.wyldcard.properties.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * A read-only value that invokes a {@link ComputedValueGetter} to fetch its synthesized value.
 */
public class ComputedReadOnlyValue implements PropertyValue {

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
