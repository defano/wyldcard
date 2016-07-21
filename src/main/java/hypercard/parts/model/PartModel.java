/**
 * PartModel.java
 * @author matt.defano@gmail.com
 * 
 * Implements a table of model associated with a partSpecifier object. Provides
 * methods for defining, getting and setting model, as well as notifying
 * listeners of changes. 
 */

package hypercard.parts.model;

import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;

import java.io.Serializable;
import java.util.*;

public class PartModel {

	private PartType type;
	private Map<String, Value> properties = new HashMap<>();
	private Map<String, Boolean> readOnly = new HashMap<>();

	private transient List<PartModelChangeListener> listeners;

	// Required to initialize transient data member when object is deserialized
	private PartModel() {
		this.listeners = new ArrayList<>();
	}

	public PartModel(PartType type) {
		this.type = type;
	}
	
	public void defineProperty (String property, Value value, boolean readOnly) {
		property = property.toLowerCase();
		
		properties.put(property, value);
		this.readOnly.put(property, readOnly);
	}
	
	public void setProperty (String property, Value value) 
	throws NoSuchPropertyException, PropertyPermissionException 
	{
		property = property.toLowerCase();
		
		if (!propertyExists(property))
			throw new NoSuchPropertyException("Can't set property " + property + " because it doesn't exist.");
		if (readOnly.get(property) == true)
			throw new PropertyPermissionException("Can't set property " + property + " because it is read only.");
		
		Value oldValue = properties.get(property);
		properties.put(property, value);
		fireModelChangeListener(property, oldValue, value);
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
		
		return properties.get(property.toLowerCase());
	}
	
    public Value getKnownProperty (String property) {
    	property = property.toLowerCase();
    	
		if (!propertyExists(property))
			throw new RuntimeException("Can't get known property " + property + " because it doesn't exist");
		
		return properties.get(property);
    }
    
	public boolean propertyExists (String property) {
		property = property.toLowerCase();
		
		return properties.containsKey(property)
			&& readOnly.containsKey(property);
	}	

	public PartType getType () {
		return type;
	}

	public void addModelChangeListener(PartModelChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<>();
		}

		listeners.add(listener);
	}

	private void fireModelChangeListener(String property, Value oldValue, Value value) {
		for (PartModelChangeListener listener : listeners)
			listener.onModelChange(property, oldValue, value);
	}
}
