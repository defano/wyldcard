package com.defano.wyldcard.properties;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public class DelegatedPropertyValue implements PropertyValue {

    private final PropertiesModel delegate;

    public DelegatedPropertyValue(PropertiesModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public Value get(ExecutionContext context, PropertiesModel model) throws HtException {
        // TODO
        return null;
    }

    @Override
    public void set(ExecutionContext context, Value v, PropertiesModel model) throws HtException {
        // TODO
    }
}
