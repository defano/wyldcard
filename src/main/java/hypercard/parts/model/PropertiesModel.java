package hypercard.parts.model;

import hypertalk.ast.common.Value;
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;

import java.util.*;

public class PropertiesModel {

    public interface ComputedSetter {

        /**
         * Computes and sets the value of a property, either by modifying the provided value in some way, or by
         * converting the set operation into constituent property writes (e.g., converting a rectangle into top, left
         * height and width coordinates).
         *
         * @param model The {@link PropertiesModel} whose property is being set.
         * @param propertyName The name of the property which is to be set.
         * @param value The requested value to be set; this method is responsible for transforming this value as
         *              required.
         * @throws HtSemanticException Thrown to indicate the property cannot accept the given/computed value.
         */
        void setComputedValue(PropertiesModel model, String propertyName, Value value) throws HtSemanticException;
    }

    public interface ComputedGetter {

        /**
         * Retrieves the value of a requested property reading and modifying some other property or properties (e.g.,
         * converting top, left, height and width coordinates into a top-left and bottom-right rectangle).
         *
         * @param model The {@link PropertiesModel} whose property is being set.
         * @param propertyName The name of the property which is to be set.
         * @return The value of the property to be returned to the requester.
         */
        Value getComputedValue(PropertiesModel model, String propertyName);
    }

    private Map<String, Value> properties = new HashMap<>();
    private List<String> immutableProperties = new ArrayList<>();

    // Transient fields will not be serialized and must be re-hydrated programmatically.
    private transient Map<String, String> propertyAliases;
    private transient Set<PropertyChangeObserver> listeners;
    private transient Map<String,ComputedGetter> computerGetters;
    private transient Map<String,ComputedSetter> computerSetters;

    // Required to initialize transient data member when object is deserialized
    public PropertiesModel() {
        listeners = new HashSet<>();
        propertyAliases = new HashMap<>();
        computerGetters = new HashMap<>();
        computerSetters = new HashMap<>();
    }

    /**
     * Defines a new property.
     *
     * @param property The name of the property; case insensitive; if a property with this name already exists, the old
     *                 property will be overwritten.
     * @param value The initial value associated with this property.
     * @param readOnly True to indicate that the property cannot be set via {@link #setProperty(String, Value)} or
     *                 {@link #setKnownProperty(String, Value)}
     */
    public void defineProperty (String property, Value value, boolean readOnly) {
        property = property.toLowerCase();
        properties.put(property, value);

        if (readOnly) {
            this.immutableProperties.add(property);
        }
    }

    /**
     * Defines an alias for a property; allows multiple names to be used interchangeably for a given property (i.e.,
     * 'rect' is the same as 'rectangle').
     *
     * @param property The name of the property to be aliased
     * @param alsoKnownAs Another name by which the property can be addressed
     */
    public void definePropertyAlias(String property, String alsoKnownAs) {
        propertyAliases.put(alsoKnownAs.toLowerCase(), property.toLowerCase());
    }

    /**
     * Defines a new writable property with a computed setter, or, overrides the setter behavior of an existing
     * property. When a value is set into this property, the computed setter function will be invoked to compute and
     * write the actual value.
     *
     * @param propertyName The name the property on which the computed setter function should apply; may be an existing
     *                     property or a new property name.
     * @param setter The function used to set the property value.
     */
    public void defineComputedSetterProperty(String propertyName, ComputedSetter setter) {
        computerSetters.put(propertyName.toLowerCase(), setter);
    }

    /**
     * Defines a new readable property with a computed getter, or, overrides the getter behavior of an existing
     * property. When this property's value is read, the computed getter function will be invoked to retrieve the
     * actual value.
     *
     * @param propertyName The name the property on which the computed getter function should apply; may be an existing
     *                     property or a new property name.
     * @param getter The function used to get the property value.
     */
    public void defineComputedGetterProperty(String propertyName, ComputedGetter getter) {
        computerGetters.put(propertyName.toLowerCase(), getter);
    }

    /**
     * Sets the value of a property.
     *
     * @param propertyName The name of the property or an alias of the property.
     * @param value The value that should be set
     *
     * @throws NoSuchPropertyException If no property (or alias) exists matching the given name
     * @throws PropertyPermissionException If an attempt to set a property marked "read only" is made
     * @throws HtSemanticException If the property cannot accept the value provided
     */
    public void setProperty (String propertyName, Value value)
            throws NoSuchPropertyException, PropertyPermissionException, HtSemanticException
    {
        propertyName = propertyName.toLowerCase();

        if (!propertyExists(propertyName)) {
            throw new NoSuchPropertyException("Can't set property " + propertyName + " because it doesn't exist.");
        }

        if (immutableProperties.contains(propertyName)) {
            throw new PropertyPermissionException("Can't set property " + propertyName + " because it is immutable.");
        }

        // If this is an alias; get the "real" name of this property
        if (propertyAliases.containsKey(propertyName)) {
            propertyName = propertyAliases.get(propertyName);
        }

        Value oldValue = getProperty(propertyName);

        if (computerSetters.keySet().contains(propertyName)) {
            computerSetters.get(propertyName).setComputedValue(this, propertyName, value);
        } else {
            properties.put(propertyName, value);
        }

        fireOnPropertyChanged(propertyName, oldValue, value);
    }

    /**
     * Sets the value of a known property; throws a RuntimeException if an attempt is made to set an undefined property.
     *
     * @param property The name of the property to set (or one of its aliases)
     * @param value The value to set
     */
    public void setKnownProperty (String property, Value value) {
        try {
            setProperty(property, value);
        } catch (NoSuchPropertyException | PropertyPermissionException | HtSemanticException e) {
            throw new RuntimeException("Can't set known property.", e);
        }
    }

    /**
     * Gets the value of a property.
     *
     * @param property The name of the property to get (or one of its aliases)
     * @return The value of the property.
     *
     * @throws NoSuchPropertyException If no property exists with this name
     */
    public Value getProperty (String property)
            throws NoSuchPropertyException
    {
        property = property.toLowerCase();

        if (!propertyExists(property)) {
            throw new NoSuchPropertyException("Can't get property " + property + " because it doesn't exist.");
        }

        // If this is an alias; get the "real" name of this property
        if (propertyAliases.containsKey(property)) {
            property = propertyAliases.get(property);
        }

        if (computerGetters.keySet().contains(property)) {
            return computerGetters.get(property).getComputedValue(this, property);
        } else {
            return properties.get(property.toLowerCase());
        }
    }

    /**
     * Gets the value of a known property; throws a RuntimeException if the property is not defined.
     * @param property The name of the property to get (or one of its aliases)
     * @return The value of the property
     */
    public Value getKnownProperty (String property) {
        property = property.toLowerCase();

        try {
            return getProperty(property);
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("Bug! Can't get known property " + property + " because it isn't defined.", e);
        }
    }

    /**
     * Determines if a given property exists.
     *
     * @param property The name of the property (or one of its aliases).
     * @return True if the property exists, false otherwise.
     */
    public boolean propertyExists (String property) {
        property = property.toLowerCase();
        return properties.containsKey(property) || propertyAliases.containsKey(property) || (computerGetters.containsKey(property) && computerSetters.containsKey(property));
    }

    /**
     * Adds an observer of property value changes.
     * @param listener The observer
     */
    public void addPropertyChangedObserver(PropertyChangeObserver listener) {
        listeners.add(listener);
    }


    private void fireOnPropertyChanged(String property, Value oldValue, Value value) {
        for (PropertyChangeObserver listener : listeners) {
            listener.onPropertyChanged(property, oldValue, value);
        }
    }
}
