package com.defano.wyldcard.properties;

import com.defano.wyldcard.properties.value.PropertyValue;
import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a WyldCard property; binds a {@link PropertyValue} to one or more names.
 */
public class Property {

    private final transient ArrayList<String> aliases;      // All names that this property is known by, alphabetized
    private final PropertyValue value;                      // The value of this property

    /**
     * Constructs a Property with a given value and one or more names/aliases.
     *
     * @param value   The value of this property.
     * @param aliases The name (or names) that can be used to refer to this property.
     */
    public Property(PropertyValue value, String... aliases) {
        if (aliases == null || aliases.length == 0) {
            throw new IllegalArgumentException("Property must have at least one name.");
        }

        if (value == null) {
            throw new IllegalArgumentException("Property must have a value.");
        }

        this.value = value;
        this.aliases = Lists.newArrayList(aliases);
        Collections.sort(this.aliases);
    }

    /**
     * Gets the primary or default name of this property.
     *
     * @return The primary name of this property.
     */
    public String name() {
        return aliases.get(0);
    }

    /**
     * Gets a list of all names associated with this property, including the primary name.
     *
     * @return A list of all names associated with this property.
     */
    public List<String> aliases() {
        return aliases;
    }

    /**
     * Adds zero or more new aliases to this property. Does not affect or replace existing aliases.
     *
     * @param aliases Additional aliases that this property should be known by.
     */
    public void addAliases(String... aliases) {
        this.aliases.addAll(Lists.newArrayList(aliases));
        Collections.sort(this.aliases);
    }

    /**
     * Gets the {@link PropertyValue} associated with this property.
     *
     * @return The value of this property.
     */
    public PropertyValue value() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(aliases, property.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aliases);
    }
}
