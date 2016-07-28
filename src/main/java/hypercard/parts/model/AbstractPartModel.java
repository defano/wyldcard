/**
 * AbstractPartModel.java
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

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class AbstractPartModel {

	public static final String PROP_SCRIPT = "script";
	public static final String PROP_ID = "id";
	public static final String PROP_NAME = "name";
	public static final String PROP_LEFT = "left";
	public static final String PROP_TOP = "top";
	public static final String PROP_WIDTH = "width";
	public static final String PROP_HEIGHT = "height";

	private PartType type;
	private Map<String, Value> properties = new HashMap<>();
	private List<String> readOnly = new ArrayList<>();
//	private Map<String, Boolean> readOnly = new HashMap<>();

	private transient List<PartModelObserver> listeners;

	// Required to initialize transient data member when object is deserialized
	private AbstractPartModel() {
		this.listeners = new ArrayList<>();
	}

	protected AbstractPartModel(PartType type) {
		this.listeners = new ArrayList<>();
		this.type = type;
	}

	public void defineProperty (String property, Value value, boolean readOnly) {
		property = property.toLowerCase();
		properties.put(property, value);

		if (readOnly) {
			this.readOnly.add(property);
		}
	}
	
	public void setProperty (String property, Value value) 
	throws NoSuchPropertyException, PropertyPermissionException 
	{
		property = property.toLowerCase();
		
		if (!propertyExists(property))
			throw new NoSuchPropertyException("Can't set property " + property + " because it doesn't exist.");
		if (readOnly.contains(property))
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
		return properties.containsKey(property);
	}	

	public PartType getType () {
		return type;
	}

	public Rectangle getRect() {
		try {
			Rectangle rect = new Rectangle();
			rect.x = getProperty(ButtonModel.PROP_LEFT).integerValue();
			rect.y = getProperty(ButtonModel.PROP_TOP).integerValue();
			rect.height = getProperty(ButtonModel.PROP_HEIGHT).integerValue();
			rect.width = getProperty(ButtonModel.PROP_WIDTH).integerValue();

			return rect;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't get geometry for part model.");
		}
	}

	public void addModelChangeListener(PartModelObserver listener) {
		listeners.add(listener);
	}

	private void fireModelChangeListener(String property, Value oldValue, Value value) {
		for (PartModelObserver listener : listeners)
			listener.onPartAttributeChanged(property, oldValue, value);
	}
}
