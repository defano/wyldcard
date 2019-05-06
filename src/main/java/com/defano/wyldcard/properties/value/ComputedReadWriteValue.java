package com.defano.wyldcard.properties.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * A {@link PropertyValue} that stores no value of its own, but rather invokes a provided {@link ComputedValueGetter}
 * and {@link ComputedValueSetter} when the property is read or written.
 */
public class ComputedReadWriteValue implements PropertyValue {

    private final transient ComputedValueGetter getter;
    private final transient ComputedValueSetter setter;

    public ComputedReadWriteValue(ComputedValueGetter getter, ComputedValueSetter setter) {
        this.getter = getter;
        this.setter = setter;
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
        setter.setComputedValue(context, model, v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ComputedReadWriteValue{" +
                "getter=" + getter +
                ", setter=" + setter +
                '}';
    }
}
