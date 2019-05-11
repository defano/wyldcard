package com.defano.wyldcard.property.builder;

/**
 * An object that builds a {@link com.defano.wyldcard.property.Property}.
 */
public interface PropertyBuilder {

    /**
     * Define a property identified by a single name.
     *
     * @param propertyName The name of the property, for example, "id" or "name".
     * @return A {@link PropertyValueBuilder} used to define the value associated with this property.
     */
    PropertyValueBuilder define(String propertyName);

    /**
     * Define a property identified by one or more names. Useful when a defining a property whose spelling can vary or
     * which allows abbreviated names.
     *
     * @param propertyNames The name and aliases of this property. For example, 'hilite', 'highlight', 'hilight'.
     * @return A {@link PropertyValueBuilder} used to define the value associated with this property.
     */
    PropertyValueBuilder define(String... propertyNames);
}
