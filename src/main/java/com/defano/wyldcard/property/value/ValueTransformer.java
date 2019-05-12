package com.defano.wyldcard.property.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.property.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface ValueTransformer {
    Value transform(ExecutionContext context, PropertiesModel model, Value rawValue) throws HtException;
}
