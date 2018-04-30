package com.defano.wyldcard.parts.model;

import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * Represents a property in a model that refers to a property of the same name in another model.
 */
public interface DelegatedProperty {
    /**
     * Get the {@link PropertiesModel} that the given property should be delegated to. Get and set operations to this
     * property will be forwarded to the returned PropertiesModel.
     *
     * Useful for properties which are inherited from another model. For example, a card's size property is inherited
     * from the stack model.
     *
     * @param property The name of the property being delegated
     * @return The PropertiesModel to which to property should be delegated
     */
    PropertiesModel getDelegatedModel(ExecutionContext context, String property);
}
