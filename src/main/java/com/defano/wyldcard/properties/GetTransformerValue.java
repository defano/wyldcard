package com.defano.wyldcard.properties;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public class GetTransformerValue implements PropertyValue {

    private final GetTransform transform;
    private Value v;

    public GetTransformerValue(GetTransform transform) {
        this.transform = transform;
    }

    @Override
    public Value get(ExecutionContext context, PropertiesModel model) throws HtException {
        return transform.get(context, model, v);
    }

    @Override
    public void set(ExecutionContext context, Value v, PropertiesModel model) throws HtException {
        this.v = v;
    }
}
