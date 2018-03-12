package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Value;

public interface DispatchComputedSetter extends ComputedSetter {
    
    @Override
    void setComputedValue(PropertiesModel model, String propertyName, Value value);
}
