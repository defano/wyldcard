package hypercard.parts.model;

import hypertalk.ast.common.Value;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matt on 12/23/16.
 */
public class PropertiesTable {

    private Map<String, Value> properties = new HashMap<>();
    private Map<String, String> propertyAliases = new HashMap<>();
    private List<String> immutableProperties = new ArrayList<>();
    private transient List<PropertyChangeObserver> listeners;

    // Required to initialize transient data member when object is deserialized
    public PropertiesTable() {
        this.listeners = new ArrayList<>();
    }

    public void defineProperty (String property, Value value, boolean readOnly) {
        property = property.toLowerCase();
        properties.put(property, value);

        if (readOnly) {
            this.immutableProperties.add(property);
        }
    }

    public void defineAlias (String property, String alsoKnownAs) {
        propertyAliases.put(alsoKnownAs.toLowerCase(), property.toLowerCase());
    }

    public void setProperty (String property, Value value)
            throws NoSuchPropertyException, PropertyPermissionException
    {
        property = property.toLowerCase();

        if (!propertyExists(property))
            throw new NoSuchPropertyException("Can't set property " + property + " because it doesn't exist.");
        if (immutableProperties.contains(property))
            throw new PropertyPermissionException("Can't set property " + property + " because it is immutable.");

        // If this is an alias; get the "real" name of this property
        if (propertyAliases.containsKey(property)) {
            property = propertyAliases.get(property);
        }

        Value oldValue = properties.get(property);
        properties.put(property, value);
        fireOnPropertyChanged(property, oldValue, value);
    }

    public void setKnownProperty (String property, Value value) {
        try {
            setProperty(property, value);
        } catch (NoSuchPropertyException | PropertyPermissionException e) {
            throw new RuntimeException("Can't set known property.", e);
        }
    }

    public Value getProperty (String property)
            throws NoSuchPropertyException
    {
        property = property.toLowerCase();

        if (!propertyExists(property))
            throw new NoSuchPropertyException("Can't get property " + property + " because it doesn't exist");

        // If this is an alias; get the "real" name of this property
        if (propertyAliases.containsKey(property)) {
            property = propertyAliases.get(property);
        }

        return properties.get(property.toLowerCase());
    }

    public Value getKnownProperty (String property) {
        property = property.toLowerCase();

        if (!propertyExists(property))
            throw new RuntimeException("Can't get known property " + property + " because it doesn't exist");

        // If this is an alias; get the "real" name of this property
        if (propertyAliases.containsKey(property)) {
            property = propertyAliases.get(property);
        }

        return properties.get(property);
    }

    public boolean propertyExists (String property) {
        property = property.toLowerCase();
        return properties.containsKey(property) || propertyAliases.containsKey(property);
    }

    public void addPropertyChangedObserver(PropertyChangeObserver listener) {
        listeners.add(listener);
    }

    private void fireOnPropertyChanged(String property, Value oldValue, Value value) {
        for (PropertyChangeObserver listener : listeners)
            listener.onPropertyChanged(property, oldValue, value);
    }
}
