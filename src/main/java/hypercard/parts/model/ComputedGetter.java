package hypercard.parts.model;

import hypertalk.ast.common.Value;

public interface ComputedGetter {

    /**
     * Retrieves the value of a requested property reading and modifying some other property or properties (e.g.,
     * converting top, left, height and width coordinates into a top-left and bottom-right rectangle).
     *
     * @param model        The {@link PropertiesModel} whose property is being set.
     * @param propertyName The name of the property which is to be set.
     * @return The value of the property to be returned to the requester.
     */
    Value getComputedValue(PropertiesModel model, String propertyName);
}
