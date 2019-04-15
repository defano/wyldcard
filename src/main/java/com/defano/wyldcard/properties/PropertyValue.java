package com.defano.wyldcard.properties;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public interface PropertyValue {

    Value get(ExecutionContext context, PropertiesModel model) throws HtException;
    void set(ExecutionContext context, Value v, PropertiesModel model) throws HtException;
}
