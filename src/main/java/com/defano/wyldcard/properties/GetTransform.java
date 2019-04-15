package com.defano.wyldcard.properties;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public interface GetTransform {
    Value get(ExecutionContext context, PropertiesModel model, Value rawValue) throws HtException;
}
