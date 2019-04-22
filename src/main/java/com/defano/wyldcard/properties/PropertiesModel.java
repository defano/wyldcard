package com.defano.wyldcard.properties;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * A properties-based data model for WyldCard parts and objects.
 */
public interface PropertiesModel {

    /**
     * Clears all defined properties from this model, but does not modify the set of observers.
     */
    void clear();

    /**
     * Adds a new {@link Property} to this model.
     * <p>
     * All properties must have case insensitive unique names and aliases. If a property with the same name (or names)
     * exists, it will be replaced by this property.
     *
     * @param property The property to be added.
     */
    void add(Property property);

    /**
     * Sets the value bound to the identified property without notifying observers that the value has been changed. This
     * method is intended to handle cases where setting a value could cause a cycle in the notification chain or
     * otherwise produce unintended side effects.
     * <p>
     * In most situations, {@link #set(ExecutionContext, String, Value)} is more appropriate.
     *
     * @param context       The execution context
     * @param propertyName  The case insensitive name of the property
     * @param propertyValue The value to set the property to.
     */
    void setQuietly(ExecutionContext context, String propertyName, Value propertyValue);

    /**
     * Sets the value bound to the identified property. This method is intended for WyldCard-internal (i.e., not
     * scripted) access to properties.
     *
     * @param context       The execution context
     * @param propertyName  The case insensitive name of the property
     * @param propertyValue
     */
    void set(ExecutionContext context, String propertyName, Value propertyValue);

    /**
     * Attempts to set the value bound to the identified property, throwing an {@link HtException} when the value cannot
     * by set, either because it doesn't exist, it isn't writable, or an error occurred writing it. This method is
     * intended for use by script/programmatic-access to property values.
     *
     * @param context       The execution context
     * @param propertyName  The case insensitive name of the property to be set
     * @param propertyValue The value to be written into the property
     * @throws HtException Thrown if the property doesn't exist, cannot be written, or an error occurs writing to it.
     */
    void trySet(ExecutionContext context, String propertyName, Value propertyValue) throws HtException;

    /**
     * Gets the value bound to the identified property. This method is intended for WyldCard-internal (i.e., not
     * scripted) access to properties.
     *
     * @param context      The execution context
     * @param propertyName The case insensitive name of the property (or one of its aliases).
     * @return The value bound to the property
     * @throws IllegalStateException    Thrown if an error occurs while evaluating the property.
     * @throws IllegalArgumentException Thrown if the property does not exist.
     */
    Value get(ExecutionContext context, String propertyName);

    /**
     * Attempts to get the value bound to the identified property, throwing an {@link HtException} when the value
     * cannot be retrieved. This method is intended for use by script/programmatic-access to property values.
     *
     * @param context      The execution context.
     * @param propertyName The case insensitive name of the property (or one of its aliases).
     * @return The value bound to this property.
     * @throws HtException Thrown if the property cannot be retreived, perhaps because it doesn't exist or an error
     *                     occurred while trying to evaluate it.
     */
    Value tryGet(ExecutionContext context, String propertyName) throws HtException;

    /**
     * Returns the property identified by the given name, or null, if no such property exists.
     *
     * @param propertyName The case insensitive name of the property (or one of its aliases).
     * @return The identified property, or null if it doesn't exist.
     */
    Property findProperty(String propertyName);

    /**
     * Determines if a property identified by a given name exists.
     *
     * @param propertyName The case insensitive name of the property (or one of its aliases).
     * @return True if the property exists, false otherwise.
     */
    boolean hasProperty(String propertyName);

    /**
     * Adds an observer of property value changes. Note that observers are always notified of property changes on the
     * Swing dispatch thread.
     *
     * @param listener The observer
     */
    void addPropertyChangedObserver(PropertyChangeObserver listener);

    void addPropertyChangedObserverAndNotify(ExecutionContext context, PropertyChangeObserver listener);

    /**
     * Invokes the {@link PropertyChangeObserver#onPropertyChanged(ExecutionContext, PropertiesModel, String, Value, Value)} method for
     * all properties on the provided observer. Useful for listeners that wish to initialize themselves with the current
     * state of the model.
     *
     * @param context                The execution context.
     * @param listener               This listener to be notified; does not have to be an active listener of this model.
     * @param includeComputedGetters When false, do not notify computed/synthesized properties
     */
    void notifyPropertyChangedObserver(ExecutionContext context, PropertyChangeObserver listener, boolean includeComputedGetters);

    /**
     * Removes an observer of property value changes; this listener will not be notified of future changes.
     *
     * @param listener The property change observer
     */
    void removePropertyChangedObserver(PropertyChangeObserver listener);
}
