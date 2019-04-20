package com.defano.wyldcard.properties;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import com.defano.hypertalk.exception.HtNoSuchPropertyException;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.properties.builder.PropertyBuilder;
import com.defano.wyldcard.properties.builder.PropertyValueBuilder;
import com.defano.wyldcard.properties.value.BasicValue;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

public class SimplePropertiesModel implements PropertiesModel, PropertyBuilder {

    // List of stored properties
    private final PropertyList properties = new PropertyList();

    // Observers (not serialized)
    private transient ArrayList<PropertyChangeObserver> propertyChangeObservers = new ArrayList<>();

    @PostConstruct
    protected void postConstructAdvancedPropertiesModel() {
        propertyChangeObservers = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        properties.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Property property) {
        Property existingProperty = findProperty(property.name());
        if (existingProperty != null) {
            properties.remove(existingProperty);
        }

        properties.add(property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyValueBuilder define(String propertyName) {
        return new PropertyValueBuilder(this, propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyValueBuilder define(String... propertyNames) {
        return new PropertyValueBuilder(this, propertyNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQuietly(ExecutionContext context, String propertyName, Value propertyValue) {
        set(context, propertyName, propertyValue, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(ExecutionContext context, String propertyName, Value propertyValue) {
        set(context, propertyName, propertyValue, true);
    }

    private void set(ExecutionContext context, String propertyName, Value propertyValue, boolean notifyObservers) {
        Property p = findProperty(propertyName);

        if (p == null) {
            throw new HtUncheckedSemanticException(new HtSemanticException("No property named '" + propertyName + "'."));
        }

        try {
            p.value().set(context, propertyValue, this);

            if (notifyObservers) {
                fireOnPropertyChanged(context, propertyName, get(context, propertyName), propertyValue);
            }

        } catch (Throwable t) {
            throw new IllegalStateException("Error setting value for property '" + propertyName + "'.", t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trySet(ExecutionContext context, String propertyName, Value propertyValue) throws HtException {
        Property p = findProperty(propertyName);

        if (p == null) {
            throw new HtNoSuchPropertyException("No such property named '" + propertyName + "'.");
        }

        p.value().set(context, propertyValue, this);
        fireOnPropertyChanged(context, propertyName, get(context, propertyName), propertyValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value get(ExecutionContext context, String propertyName) {
        Property p = findProperty(propertyName);

        if (p == null) {
            throw new IllegalArgumentException("No such property named '" + propertyName + "'.");
        }

        try {
            return p.value().get(context, this);
        } catch (Throwable t) {
            throw new IllegalStateException("Error retrieving value for property '" + propertyName + "'.", t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value tryGet(ExecutionContext context, String propertyName) throws HtException {
        Property p = findProperty(propertyName);

        if (p == null) {
            throw new HtNoSuchPropertyException("No such property named '" + propertyName + "'.");
        }

        return p.value().get(context, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property findProperty(String propertyName) {
        String canonicalName = propertyName.toLowerCase();
        return properties.stream()
                .filter(p -> p.aliases().contains(canonicalName))
                .findFirst()
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasProperty(String propertyName) {
        return findProperty(propertyName) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPropertyChangedObserver(PropertyChangeObserver listener) {
        propertyChangeObservers.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyPropertyChangedObserver(ExecutionContext context, PropertyChangeObserver listener, boolean includeComputedGetters) {
        Invoke.onDispatch(() -> {
            for (Property thisProperty : properties) {
                if (thisProperty.value() instanceof BasicValue || includeComputedGetters) {
                    try {
                        listener.onPropertyChanged(context, this, thisProperty.name(), thisProperty.value().get(context, this), thisProperty.value().get(context, this));
                    } catch (HtException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePropertyChangedObserver(PropertyChangeObserver listener) {
        propertyChangeObservers.remove(listener);
    }

    private void fireOnPropertyChanged(ExecutionContext context, String property, Value oldValue, Value value) {
        Invoke.onDispatch(() -> propertyChangeObservers.forEach(l -> l.onPropertyChanged(context, this, property, oldValue, value)));
    }

}
