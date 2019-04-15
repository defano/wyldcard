package com.defano.wyldcard.properties;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public class BasicProperty implements PropertyValue {

    private Value v;

    public BasicProperty(Value v) {
        this.v = v;
    }

    @Override
    public Value get(ExecutionContext context, PropertiesModel model) throws HtException {
        return v;
    }

    @Override
    public void set(ExecutionContext context, Value v, PropertiesModel model) throws HtException {
        this.v = v;
    }
}
