package com.defano.hypercard.parts.model;

/**
 * Represents a property in a model that refers to another model.
 */
public interface DelegatedProperty {
    /**
     * Get the model in which the given property should be delegated to. Gets and sets of this property will
     * be forwarded to the returned PropertiesModel.
     * @param property The name of the property being delegated
     * @return The PropertiesModel to which to property should be delegated
     */
    PropertiesModel getDelegatedModel(String property);
}
