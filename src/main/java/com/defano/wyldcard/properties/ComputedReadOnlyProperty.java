package com.defano.wyldcard.properties;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.model.ComputedGetter;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public class ComputedReadOnlyProperty implements PropertyValue {

    private final ComputedGetter getter;

    public ComputedReadOnlyProperty(ComputedGetter getter) {
        this.getter = getter;
    }

    @Override
    public Value get(ExecutionContext context, PropertiesModel model) {
        return getter.getComputedValue(context, model);
    }

    @Override
    public void set(ExecutionContext context, Value v, PropertiesModel model) throws HtSemanticException {
        throw new HtSemanticException("Cannot set this property.");
    }
}
