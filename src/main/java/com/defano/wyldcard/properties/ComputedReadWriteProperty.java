package com.defano.wyldcard.properties;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.model.ComputedGetter;
import com.defano.wyldcard.parts.model.ComputedSetter;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public class ComputedReadWriteProperty implements PropertyValue {

    private final ComputedGetter getter;
    private final ComputedSetter setter;

    public ComputedReadWriteProperty(ComputedGetter getter, ComputedSetter setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Value get(ExecutionContext context, PropertiesModel model) {
        return getter.getComputedValue(context, model);
    }

    @Override
    public void set(ExecutionContext context, Value v, PropertiesModel model) throws HtSemanticException {
        setter.setComputedValue(context, model, v);
    }
}
