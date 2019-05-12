package com.defano.wyldcard.property;

import com.defano.wyldcard.property.value.PropertyValue;
import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.*;

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
     * Determines if the property is known by the given name. That is, whether the provided name matches the property's
     * name or one of its aliases (case sensitive).
     *
     * @param name The name to match
     * @return True if the given argument matches the name of this property or one of its aliases.
     */
    public boolean matches(String name) {
        return aliases.contains(name);
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

    /**
     * Two properties are considered equal if their set of aliases are equal (irrespective of their value).
     *
     * @param o The object to compare for equality with this.
     * @return True if their names/aliases are identical, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(aliases, property.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aliases.toArray());
    }
}
