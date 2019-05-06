package com.defano.wyldcard.properties.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface ComputedValueGetter {

    /**
     * Retrieves the value of a requested property, possibly by reading and modifying some other property or properties
     * in the model (e.g., converting top, left, height and width coordinates into a top-left and bottom-right
     * rectangle).
     *
     * @param context      The execution context
     * @param model        The {@link PropertiesModel} whose property is being retrieved.
     * @return The value of the property to be returned to the requester.
     */
    Value getComputedValue(ExecutionContext context, PropertiesModel model);
}
