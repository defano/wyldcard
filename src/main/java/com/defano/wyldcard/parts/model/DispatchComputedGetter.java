package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Value;

/**
 * A computer property getter that always executes on the Swing dispatch thread.
 */
public interface DispatchComputedGetter extends ComputedGetter {

    /**
     * Retrieves the value of a requested property, possibly by reading and modifying some other property or properties
     * in the model (e.g., converting top, left, height and width coordinates into a top-left and bottom-right
     * rectangle).
     *
     * Differs from {@link ComputedGetter} only in that this method will be run on the dispatch thread, even if the call
     * to retrieve the property is not.
     *
     * @param model        The {@link PropertiesModel} whose property is being retrieved.
     * @param propertyName The name of the property which is to be calculated.
     * @return The value of the property to be returned to the requester.
     */
    Value getComputedValue(PropertiesModel model, String propertyName);
}
