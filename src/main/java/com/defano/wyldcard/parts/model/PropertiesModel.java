package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.NoSuchPropertyException;
import com.defano.hypertalk.exception.PropertyPermissionException;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.Collection;

public interface PropertiesModel {
    /**
     * Defines a new property.
     *
     * @param property The name of the property; case insensitive; if a property with this name already exists, the old
     *                 property will be overwritten.
     * @param value The initial value associated with this property.
     * @param readOnly True to indicate that the property cannot be set via {@link #setProperty(ExecutionContext, String, Value)} or
     *                 {@link #setKnownProperty(ExecutionContext, String, Value)}
     */
    void newProperty(String property, Value value, boolean readOnly);

    /**
     * Delegates gets/sets of a property to another model. Useful in cases where one part inherits a property from
     * another part (i.e., the rectangle of a card is actually defined by its stack).
     *
     * @param property The name of the delegated property
     * @param delegatedProperty The delegate to which requests should be forwarded.
     */
    void delegateProperty(String property, DelegatedProperty delegatedProperty);

    /**
     * Delegates a collection of properties to another properties model. See
     * {@link #delegateProperty(String, DelegatedProperty)}.
     */
    void delegateProperties(Collection<String> properties, DelegatedProperty delegatedProperty);

    /**
     * Defines an alias for a property; allows multiple names to be used interchangeably for a given property (i.e.,
     * 'rect' is the same as 'rectangle').
     *
     * @param property The name of the property to be aliased
     * @param alsoKnownAs Another nam(s) by which the property can be addressed
     */
    void newPropertyAlias(String property, String... alsoKnownAs);

    /**
     * Defines a new writable property with a computed setter, or, overrides the write behavior of an existing
     * property. When a value is set into this property, the computed setter function will be invoked to compute and
     * take whatever action is required to persist the value.
     *
     * Note that write-only properties are disallowed. Created a computer-setter requires a corresponding
     * call to {@link #newComputedGetterProperty(String, ComputedGetter)} or
     * {@link #newProperty(String, Value, boolean)}.
     *
     * @param propertyName The name the property on which the computed setter function should apply; may be an existing
     *                     property or a new property name.
     * @param setter The function used to set the property value.
     */
    void newComputedSetterProperty(String propertyName, ComputedSetter setter);

    /**
     * Defines a new readable property with a computed getter, or, overrides the read behavior of an existing
     * property. When this property's value is read, the computed getter function will be invoked to retrieve the
     * actual value.
     *
     * Note, for computed read-only properties, use Use {@link #newComputedReadOnlyProperty(String, ComputedGetter)}
     * instead.
     *
     * @param propertyName The name the property on which the computed getter function should apply; may be an existing
     *                     property or a new property name.
     * @param getter The function used to get the property value.
     */
    void newComputedGetterProperty(String propertyName, ComputedGetter getter);

    /**
     * Defines a new, read-only property whose value is computed each time the value is requested.
     *
     * @param propertyName The name of the new read-only property
     * @param getter The compute function invoked to determine the property's value
     */
    void newComputedReadOnlyProperty(String propertyName, ComputedGetter getter);

    /**
     * Sets the value of a property.
     *
     *
     * @param context The execution context
     * @param propertyName The name of the property or an alias of the property.
     * @param value The value that should be set
     *
     * @throws NoSuchPropertyException If no property (or alias) exists matching the given name
     * @throws PropertyPermissionException If an attempt to set a property marked "read only" is made
     * @throws HtSemanticException If the property cannot accept the value provided
     */
    void setProperty(ExecutionContext context, String propertyName, Value value) throws HtSemanticException;

    /**
     * Sets the value of a known property; has no effect if property does not actually exist. This method exists for
     * programmatic modifications of properties; HyperTalk modification of properties should use
     * {@link #setProperty(ExecutionContext, String, Value)} which produces an exception if a script attempts to write a non-existent
     * property.
     *
     * @param context The execution context.
     * @param property The name of the property to set (or one of its aliases)
     * @param value The value to set
     */
    void setKnownProperty(ExecutionContext context, String property, Value value);

    /**
     * Sets the value of a known property; has no effect if property does not actually exist. This method exists for
     * programmatic modifications of properties; HyperTalk modification of properties should use
     * {@link #setProperty(ExecutionContext, String, Value)} which produces an exception if a script attempts to write a non-existent
     * property.
     *
     * @param context The execution context.
     * @param property The name of the property to set (or one of its aliases)
     * @param value The value to set
     * @param quietly When true, observers of this model will not be notified of the change.
     */
    void setKnownProperty(ExecutionContext context, String property, Value value, boolean quietly);

    /**
     * Gets the "raw" value of the given property. That is, does not account for aliases, delegates, or computed
     * values.
     * @param property The name of the property.
     * @return The raw value associated with this property or null, if no raw value exists.
     */
    Value getRawProperty(String property);

    /**
     * Gets the value of a property.
     *
     * @param context The execution context.
     * @param property The name of the property to get (or one of its aliases)
     * @return The value of the property.
     *
     * @throws NoSuchPropertyException If no property exists with this name
     */
    Value getProperty(ExecutionContext context, String property) throws NoSuchPropertyException;

    /**
     * Gets the value of a known property; returns a new value if the property doesn't exist.
     *
     * @param context The execution context.
     * @param property The name of the property to get (or one of its aliases)
     * @return The value of the property
     */
    Value getKnownProperty(ExecutionContext context, String property);

    /**
     * Determines if a given property exists.
     *
     * @param property The name of the property (or one of its aliases).
     * @return True if the property exists, false otherwise.
     */
    boolean hasProperty(String property);

    /**
     * Adds an observer of property will-change events.
     * @param listener The observer
     */
    void addPropertyWillChangeObserver(PropertyWillChangeObserver listener);

    /**
     * Adds an observer of property value changes.
     * @param listener The observer
     */
    void addPropertyChangedObserver(PropertyChangeObserver listener);

    /**
     * Invokes the {@link PropertyChangeObserver#onPropertyChanged(ExecutionContext, WyldCardPropertiesModel, String, Value, Value)} method for
     * all properties on the provided observer. Useful for listeners that wish to initialize themselves with the current
     * state of the model.
     *
     * @param context The execution context.
     * @param listener This listener to be notified; does not have to be an active listener of this model.
     */
    void notifyPropertyChangedObserver(ExecutionContext context, PropertyChangeObserver listener);

    boolean removePropertyChangedObserver(PropertyChangeObserver listener);
}
