/**
 * Properties.java
 * @author matt.defano@gmail.com
 * 
 * Implements a table of properties associated with a part object. Provides
 * methods for defining, getting and setting properties, as well as notifying
 * listeners of changes. 
 */

package hypertalk.properties;

import hypercard.parts.Part;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartIdSpecifier;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Properties implements Serializable {
private static final long serialVersionUID = -4729636262151414377L;

	private Part part;
	private Map<String, Value> values;
	private Map<String, Boolean> permissions;

	private Vector<PropertyChangeListener> listeners;
	
	public Properties (Part part) {
		this.part = part;

		values = new HashMap<String, Value>();
		permissions = new HashMap<String, Boolean>();
		listeners = new Vector<PropertyChangeListener>();
	}
	
	public void defineProperty (String property, Value value, boolean readOnly) {
		property = property.toLowerCase();
		
		values.put(property, value);
		permissions.put(property, readOnly);
	}
	
	public void setProperty (String property, Value value) 
	throws NoSuchPropertyException, PropertyPermissionException 
	{
		property = property.toLowerCase();
		
		if (!propertyExists(property))
			throw new NoSuchPropertyException("Can't set property " + property + " because it doesn't exist");
		if (permissions.get(property) == true)
			throw new PropertyPermissionException("Can't set property " + property + " because it is read only");
		
		Value oldValue = values.get(property);
		values.put(property, value);
		firePropertyChangeListener(property, oldValue, value);
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
		
		return values.get(property.toLowerCase());
	}
	
    public Value getKnownProperty (String property) {
    	property = property.toLowerCase();
    	
		if (!propertyExists(property))
			throw new RuntimeException("Can't get known property " + property + " because it doesn't exist");
		
		return values.get(property);        
    }
    
	public boolean propertyExists (String property) {
		property = property.toLowerCase();
		
		return values.containsKey(property) 
			&& permissions.containsKey(property);
	}	
	
	public void addPropertyChangeListener (PropertyChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removePropertyChangeListener (PropertyChangeListener listener) {
		listeners.remove(listener);
	}
	
	private void firePropertyChangeListener (String property, Value oldValue, Value value) {
		PartSpecifier ps = new PartIdSpecifier(part.getType(), part.getId());
		
		for (PropertyChangeListener listener : listeners)
			listener.propertyChanged(ps, property, oldValue, value);
	}
}
