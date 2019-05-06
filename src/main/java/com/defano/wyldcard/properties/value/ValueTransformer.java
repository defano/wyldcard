package com.defano.wyldcard.properties.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface ValueTransformer {
    Value transform(ExecutionContext context, PropertiesModel model, Value rawValue) throws HtException;
}
