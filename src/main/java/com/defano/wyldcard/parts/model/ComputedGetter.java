package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * Represents a readable attribute whose value is computationally derived.
 */
public interface ComputedGetter {

    /**
     * Retrieves the value of a requested property, possibly by reading and modifying some other property or properties
     * in the model (e.g., converting top, left, height and width coordinates into a top-left and bottom-right
     * rectangle).
     *
     *
     * @param context      The execution context
     * @param model        The {@link PropertiesModel} whose property is being retrieved.
     * @param propertyName The name of the property which is to be calculated.
     * @return The value of the property to be returned to the requester.
     */
    Value getComputedValue(ExecutionContext context, PropertiesModel model, String propertyName);
}
