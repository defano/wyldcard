package com.defano.wyldcard.parts.model;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.NoSuchPropertyException;
import com.defano.hypertalk.exception.PropertyPermissionException;
import com.google.common.collect.Sets;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A model of HyperTalk-addressable properties that provides observability, derived getters and setters, and read-only
 * attributes.
 */
public class PropertiesModel {

    // Properties which can be read/set by HyperTalk
    private final Map<String, Value> properties = new ConcurrentHashMap<>();

    // Properties which are readable, but not writable via HyperTalk (i.e., part id)
    private final Set<String> immutableProperties = Sets.newConcurrentHashSet();

    // Transient fields will not be serialized and must be re-hydrated programmatically in @PostConstruct.
    private transient Map<String, String> propertyAliases;
    private transient Set<PropertyChangeObserver> changeObservers;
    private transient Set<PropertyWillChangeObserver> willChangeObservers;
    private transient Map<String,ComputedGetter> computerGetters;
    private transient Map<String,ComputedSetter> computerSetters;
    private transient Map<String,DelegatedProperty> delegatedProperties;

    // Required to initialize transient data member when object is de-serialized
    public PropertiesModel() {
        initialize();
    }

    @PostConstruct
    protected void initialize() {
        propertyAliases = new ConcurrentHashMap<>();
        changeObservers = Sets.newConcurrentHashSet();
        willChangeObservers = Sets.newConcurrentHashSet();
        computerGetters = new ConcurrentHashMap<>();
        computerSetters = new ConcurrentHashMap<>();
        delegatedProperties = new ConcurrentHashMap<>();
    }

    /**
     * Defines a new property.
     *
     * @param property The name of the property; case insensitive; if a property with this name already exists, the old
     *                 property will be overwritten.
     * @param value The initial value associated with this property.
     * @param readOnly True to indicate that the property cannot be set via {@link #setProperty(ExecutionContext, String, Value)} or
     *                 {@link #setKnownProperty(ExecutionContext, String, Value)}
     */
    public void defineProperty (String property, Value value, boolean readOnly) {
        assertConstructed();

        property = property.toLowerCase();
        properties.put(property, value);

        if (readOnly) {
            this.immutableProperties.add(property);
        } else {
            this.immutableProperties.remove(property);
        }
    }

    /**
     * Delegates gets/sets of a property to another model. Useful in cases where one part inherits a property from
     * another part (i.e., the rectangle of a card is actually defined by its stack).
     *
     * @param property The name of the delegated property
     * @param delegatedProperty The delegate to which requests should be forwarded.
     */
    public void delegateProperty(String property, DelegatedProperty delegatedProperty) {
        assertConstructed();
        delegatedProperties.put(property.toLowerCase(), delegatedProperty);
    }

    /**
     * Delegates a collection of properties to another properties model. See
     * {@link #delegateProperty(String, DelegatedProperty)}.
     */
    public void delegateProperties(Collection<String> properties, DelegatedProperty delegatedProperty) {
        assertConstructed();

        for (String thisProperty : properties) {
            delegateProperty(thisProperty, delegatedProperty);
        }
    }

    /**
     * Defines an alias for a property; allows multiple names to be used interchangeably for a given property (i.e.,
     * 'rect' is the same as 'rectangle').
     *
     * @param property The name of the property to be aliased
     * @param alsoKnownAs Another nam(s) by which the property can be addressed
     */
    public void definePropertyAlias(String property, String... alsoKnownAs) {
        assertConstructed();

        for (String thisAka : alsoKnownAs) {
            propertyAliases.put(thisAka.toLowerCase(), property.toLowerCase());
        }
    }

    /**
     * Defines a new writable property with a computed setter, or, overrides the write behavior of an existing
     * property. When a value is set into this property, the computed setter function will be invoked to compute and
     * take whatever action is required to persist the value.
     *
     * Note that write-only properties are disallowed. Created a computer-setter requires a corresponding
     * call to {@link #defineComputedGetterProperty(String, ComputedGetter)} or
     * {@link #defineProperty(String, Value, boolean)}.
     *
     * @param propertyName The name the property on which the computed setter function should apply; may be an existing
     *                     property or a new property name.
     * @param setter The function used to set the property value.
     */
    public void defineComputedSetterProperty(String propertyName, ComputedSetter setter) {
        assertConstructed();
        computerSetters.put(propertyName.toLowerCase(), setter);
    }

    /**
     * Defines a new readable property with a computed getter, or, overrides the read behavior of an existing
     * property. When this property's value is read, the computed getter function will be invoked to retrieve the
     * actual value.
     *
     * Note, for computed read-only properties, use Use {@link #defineComputedReadOnlyProperty(String, ComputedGetter)}
     * instead.
     *
     * @param propertyName The name the property on which the computed getter function should apply; may be an existing
     *                     property or a new property name.
     * @param getter The function used to get the property value.
     */
    public void defineComputedGetterProperty(String propertyName, ComputedGetter getter) {
        assertConstructed();
        computerGetters.put(propertyName.toLowerCase(), getter);
    }

    /**
     * Defines a new, read-only property whose value is computed each time the value is requested.
     *
     * @param propertyName The name of the new read-only property
     * @param getter The compute function invoked to determine the property's value
     */
    public void defineComputedReadOnlyProperty(String propertyName, ComputedGetter getter) {
        assertConstructed();
        computerGetters.put(propertyName.toLowerCase(), getter);
        computerSetters.put(propertyName.toLowerCase(), (context, model, property, value) -> {
            throw new PropertyPermissionException("Cannot set the property " + property + " because it is immutable.");
        });
    }

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
    public void setProperty(ExecutionContext context, String propertyName, Value value) throws HtSemanticException
    {
        setProperty(context, propertyName, value, false);
    }

    private void setProperty(ExecutionContext context, String propertyName, Value value, boolean quietly) throws HtSemanticException
    {
        assertConstructed();
        propertyName = propertyName.toLowerCase();

        if (delegatedProperties.containsKey(propertyName)) {
            delegatedProperties.get(propertyName).getDelegatedModel(context, propertyName).setProperty(context, propertyName, value, quietly);
            return;
        }

        if (!hasProperty(propertyName)) {
            throw new NoSuchPropertyException("No such property " + propertyName + ".");
        }

        if (immutableProperties.contains(propertyName)) {
            throw new PropertyPermissionException("Can't set " + propertyName + ".");
        }

        // If this is an alias; get the "real" name of this property
        if (propertyAliases.containsKey(propertyName)) {
            propertyName = propertyAliases.get(propertyName);
        }

        Value oldValue = getProperty(context, propertyName);

        if (!quietly) {
            fireOnPropertyWillChange(propertyName, oldValue, value);
        }

        if (computerSetters.keySet().contains(propertyName)) {
            ComputedSetter setter = computerSetters.get(propertyName);
            if (setter instanceof DispatchComputedSetter) {
                String finalPropertyName = propertyName;
                ThreadUtils.invokeAndWaitAsNeeded(() -> {
                    ((DispatchComputedSetter) setter).setComputedValue(context, PropertiesModel.this, finalPropertyName, value);
                });
            } else {
                setter.setComputedValue(context, this, propertyName, value);
            }
        } else {
            properties.put(propertyName, value);
        }

        if (!quietly) {
            fireOnPropertyChanged(context, propertyName, oldValue, value);
        }
    }

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
    public void setKnownProperty(ExecutionContext context, String property, Value value) {
        assertConstructed();
        setKnownProperty(context, property, value, false);
    }

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
    public void setKnownProperty(ExecutionContext context, String property, Value value, boolean quietly) {
        assertConstructed();

        try {
            setProperty(context, property, value, quietly);
        } catch (HtSemanticException e) {
            // Nothing to do
        }
    }

    /**
     * Gets the "raw" value of the given property. That is, does not account for aliases, delegates, or computed
     * values.
     * @param property The name of the property.
     * @return The raw value associated with this property or null, if no raw value exists.
     */
    public Value getRawProperty(String property) {
        assertConstructed();
        return properties.get(property.toLowerCase());
    }

    /**
     * Gets the value of a property.
     *
     * @param context The execution context.
     * @param property The name of the property to get (or one of its aliases)
     * @return The value of the property.
     *
     * @throws NoSuchPropertyException If no property exists with this name
     */
    public Value getProperty(ExecutionContext context, String property) throws NoSuchPropertyException {
        assertConstructed();

        property = property.toLowerCase();

        if (delegatedProperties.containsKey(property)) {
            return delegatedProperties.get(property).getDelegatedModel(context, property).getProperty(context, property);
        }

        if (!hasProperty(property)) {
            throw new NoSuchPropertyException("No such property " + property + ".");
        }

        // If this is an alias; get the "real" name of this property
        if (propertyAliases.containsKey(property)) {
            property = propertyAliases.get(property);
        }

        if (computerGetters.keySet().contains(property)) {
            if (computerGetters.get(property) instanceof DispatchComputedGetter) {
                final Value[] value = new Value[1];
                String finalProperty = property;
                ThreadUtils.invokeAndWaitAsNeeded(() -> value[0] = computerGetters.get(finalProperty).getComputedValue(context, this, finalProperty));
                return value[0];
            } else {
                return computerGetters.get(property).getComputedValue(context, this, property);
            }
        } else {
            return properties.get(property.toLowerCase());
        }
    }

    /**
     * Gets the value of a known property; returns a new value if the property doesn't exist.
     *
     * @param context The execution context.
     * @param property The name of the property to get (or one of its aliases)
     * @return The value of the property
     */
    public Value getKnownProperty(ExecutionContext context, String property) {
        assertConstructed();
        property = property.toLowerCase();

        try {
            return getProperty(context, property);
        } catch (NoSuchPropertyException e) {
            defineProperty(property, new Value(), false);
            e.printStackTrace();
            return new Value();
        }
    }

    /**
     * Determines if a given property exists.
     *
     * @param property The name of the property (or one of its aliases).
     * @return True if the property exists, false otherwise.
     */
    public boolean hasProperty(String property) {
        assertConstructed();
        property = property.toLowerCase();
        return properties.containsKey(property) || propertyAliases.containsKey(property) || (computerGetters.containsKey(property) && computerSetters.containsKey(property));
    }

    /**
     * Adds an observer of property will-change events.
     * @param listener The observer
     */
    public void addPropertyWillChangeObserver(PropertyWillChangeObserver listener) {
        assertConstructed();
        willChangeObservers.add(listener);
    }

    /**
     * Adds an observer of property value changes.
     * @param listener The observer
     */
    public void addPropertyChangedObserver(PropertyChangeObserver listener) {
        assertConstructed();
        changeObservers.add(listener);
    }

    /**
     * Invokes the {@link PropertyChangeObserver#onPropertyChanged(ExecutionContext, PropertiesModel, String, Value, Value)} method for
     * all properties on the provided observer. Useful for listeners that wish to initialize themselves with the current
     * state of the model.
     *
     * @param context The execution context.
     * @param listener This listener to be notified; does not have to be an active listener of this model.
     */
    public void notifyPropertyChangedObserver(ExecutionContext context, PropertyChangeObserver listener) {
        assertConstructed();
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (String property : properties.keySet()) {
                listener.onPropertyChanged(context, this, property, properties.get(property), properties.get(property));
            }
        });
    }

    public boolean removePropertyChangedObserver(PropertyChangeObserver listener) {
        assertConstructed();
        return changeObservers.remove(listener);
    }

    private void fireOnPropertyWillChange(String property, Value oldValue, Value value) {
        for (PropertyWillChangeObserver listener : willChangeObservers) {
            listener.onPropertyWillChange(property, oldValue, value);
        }
    }

    private void fireOnPropertyChanged(ExecutionContext context, String property, Value oldValue, Value value) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (Object listener : this.changeObservers.toArray()) {
                ((PropertyChangeObserver) listener).onPropertyChanged(context, this, property, oldValue, value);
            }
        });
    }

    private void assertConstructed() {
        if (propertyAliases == null || changeObservers == null || willChangeObservers == null || computerGetters == null || computerSetters == null || delegatedProperties == null) {
            throw new IllegalStateException("PropertiesModel is not properly constructed. Did you forget to call super.initialize()");
        }
    }
}
