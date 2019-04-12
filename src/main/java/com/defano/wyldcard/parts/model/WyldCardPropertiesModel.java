package com.defano.wyldcard.parts.model;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
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
public class WyldCardPropertiesModel implements PropertiesModel {

    // Properties which can be read/set by HyperTalk
    private final Map<String, Value> properties = new ConcurrentHashMap<>();

    // Properties which are readable, but not writable via HyperTalk (i.e., part id)
    private final Set<String> immutableProperties = Sets.newConcurrentHashSet();

    // Transient fields will not be serialized and must be re-hydrated programmatically in @PostConstruct.
    private transient Map<String, String> propertyAliases;
    private transient Set<PropertyChangeObserver> changeObservers;
    private transient Set<PropertyWillChangeObserver> willChangeObservers;
    private transient Map<String, ComputedGetter> computedGetters;
    private transient Map<String, ComputedSetter> computedSetters;
    private transient Map<String, DelegatedProperty> delegatedProperties;

    // Required to initialize transient data member when object is de-serialized
    public WyldCardPropertiesModel() {
        initialize();
    }

    @PostConstruct
    protected void initialize() {
        propertyAliases = new ConcurrentHashMap<>();
        changeObservers = Sets.newConcurrentHashSet();
        willChangeObservers = Sets.newConcurrentHashSet();
        computedGetters = new ConcurrentHashMap<>();
        computedSetters = new ConcurrentHashMap<>();
        delegatedProperties = new ConcurrentHashMap<>();
    }

    @Override
    public void clear() {
        properties.clear();
        immutableProperties.clear();
        propertyAliases.clear();
        changeObservers.clear();
        willChangeObservers.clear();
        computedSetters.clear();
        computedGetters.clear();
        delegatedProperties.clear();
    }

    @Override
    public void newProperty(String property, Value value, boolean readOnly) {
        assertConstructed();

        property = property.toLowerCase();
        properties.put(property, value);

        if (readOnly) {
            this.immutableProperties.add(property);
        } else {
            this.immutableProperties.remove(property);
        }
    }

    @Override
    public void delegateProperty(String property, DelegatedProperty delegatedProperty) {
        assertConstructed();
        delegatedProperties.put(property.toLowerCase(), delegatedProperty);
    }

    @Override
    public void delegateProperties(Collection<String> properties, DelegatedProperty delegatedProperty) {
        assertConstructed();

        for (String thisProperty : properties) {
            delegateProperty(thisProperty, delegatedProperty);
        }
    }

    @Override
    public void newPropertyAlias(String property, String... alsoKnownAs) {
        assertConstructed();

        for (String thisAka : alsoKnownAs) {
            propertyAliases.put(thisAka.toLowerCase(), property.toLowerCase());
        }
    }

    @Override
    public void newComputedSetterProperty(String propertyName, ComputedSetter setter) {
        assertConstructed();
        computedSetters.put(propertyName.toLowerCase(), setter);
    }

    @Override
    public void newComputedGetterProperty(String propertyName, ComputedGetter getter) {
        assertConstructed();
        computedGetters.put(propertyName.toLowerCase(), getter);
    }

    @Override
    public void newComputedReadOnlyProperty(String propertyName, ComputedGetter getter) {
        assertConstructed();
        computedGetters.put(propertyName.toLowerCase(), getter);
        computedSetters.put(propertyName.toLowerCase(), (context, model, property, value) -> {
            throw new PropertyPermissionException("Cannot set the property " + property + " because it is immutable.");
        });
    }

    @Override
    public void setProperty(ExecutionContext context, String propertyName, Value value) throws HtSemanticException {
        setProperty(context, propertyName, value, false);
    }

    private void setProperty(ExecutionContext context, String propertyName, Value value, boolean quietly) throws HtSemanticException {
        assertConstructed();
        propertyName = propertyName.toLowerCase();

        if (delegatedProperties.containsKey(propertyName)) {
            delegatedProperties.get(propertyName).getDelegatedModel(context, propertyName).setProperty(context, propertyName, value, quietly);
            return;
        }

        if (!hasProperty(propertyName)) {
            throw new NoSuchPropertyException(propertyName + " is not a WyldCard property.");
        }

        if (immutableProperties.contains(propertyName)) {
            throw new PropertyPermissionException(propertyName + " can only be read.");
        }

        // If this is an alias; get the "real" name of this property
        if (propertyAliases.containsKey(propertyName)) {
            propertyName = propertyAliases.get(propertyName);
        }

        Value oldValue = getProperty(context, propertyName);

        if (!quietly) {
            fireOnPropertyWillChange(context, propertyName, oldValue, value);
        }

        if (computedSetters.keySet().contains(propertyName)) {
            ComputedSetter setter = computedSetters.get(propertyName);
            if (setter instanceof DispatchComputedSetter) {
                String finalPropertyName = propertyName;
                Invoke.onDispatch(() -> ((DispatchComputedSetter) setter).setComputedValue(context, WyldCardPropertiesModel.this, finalPropertyName, value));
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

    @Override
    public void setKnownProperty(ExecutionContext context, String property, Value value) {
        assertConstructed();
        setKnownProperty(context, property, value, false);
    }

    @Override
    public void setKnownProperty(ExecutionContext context, String property, Value value, boolean quietly) {
        assertConstructed();

        try {
            setProperty(context, property, value, quietly);
        } catch (HtSemanticException e) {
            // Nothing to do
        }
    }

    @Override
    public Value getRawProperty(String property) {
        assertConstructed();
        return properties.get(property.toLowerCase());
    }

    @Override
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

        if (computedGetters.keySet().contains(property)) {
            if (computedGetters.get(property) instanceof DispatchComputedGetter) {
                final Value[] value = new Value[1];
                String finalProperty = property;
                Invoke.onDispatch(() -> value[0] = computedGetters.get(finalProperty).getComputedValue(context, this, finalProperty));
                return value[0];
            } else {
                return computedGetters.get(property).getComputedValue(context, this, property);
            }
        } else {
            return properties.get(property.toLowerCase());
        }
    }

    @Override
    public Value getKnownProperty(ExecutionContext context, String property) {
        assertConstructed();
        property = property.toLowerCase();

        try {
            return getProperty(context, property);
        } catch (NoSuchPropertyException e) {
            newProperty(property, new Value(), false);
            e.printStackTrace();
            return new Value();
        }
    }

    @Override
    public boolean hasProperty(String property) {
        assertConstructed();
        property = property.toLowerCase();
        return properties.containsKey(property) || propertyAliases.containsKey(property) || (computedGetters.containsKey(property) && computedSetters.containsKey(property));
    }

    @Override
    public void addPropertyWillChangeObserver(PropertyWillChangeObserver listener) {
        assertConstructed();
        willChangeObservers.add(listener);
    }

    @Override
    public void addPropertyChangedObserver(PropertyChangeObserver listener) {
        assertConstructed();
        changeObservers.add(listener);
    }

    @Override
    public void notifyPropertyChangedObserver(ExecutionContext context, PropertyChangeObserver listener, boolean includeComputedProperties) {
        assertConstructed();
        Invoke.onDispatch(() -> {

            for (String property : properties.keySet()) {
                listener.onPropertyChanged(context, this, property, properties.get(property), properties.get(property));
            }


            if (includeComputedProperties) {
                for (String property : computedGetters.keySet()) {
                    Value computedValue = computedGetters.get(property).getComputedValue(context, this, property);
                    listener.onPropertyChanged(context, this, property, computedValue, computedValue);
                }
            }
        });
    }

    @Override
    public void removePropertyChangedObserver(PropertyChangeObserver listener) {
        assertConstructed();
        changeObservers.remove(listener);
    }

    private void fireOnPropertyWillChange(ExecutionContext context, String property, Value oldValue, Value value) {
        for (PropertyWillChangeObserver listener : willChangeObservers) {
            listener.onPropertyWillChange(context, property, oldValue, value);
        }
    }

    private void fireOnPropertyChanged(ExecutionContext context, String property, Value oldValue, Value value) {
        Invoke.onDispatch(() -> {
            for (Object listener : this.changeObservers.toArray()) {
                ((PropertyChangeObserver) listener).onPropertyChanged(context, this, property, oldValue, value);
            }
        });
    }

    private void assertConstructed() {
        if (propertyAliases == null || changeObservers == null || willChangeObservers == null || computedGetters == null || computedSetters == null || delegatedProperties == null) {
            throw new IllegalStateException("DefaultPropertiesModel is not properly constructed. Did you forget to call super.initialize()");
        }
    }
}
