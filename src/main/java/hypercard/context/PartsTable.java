/**
 * PartsTable.java
 * @author matt.defano@gmail.com
 * 
 * This class implements a table of HyperCard parts (buttons, fields, etc). Provides
 * methods for adding, removing or modifying the name of existing parts. 
 */

package hypercard.context;

import hypercard.parts.Part;
import hypercard.parts.PartException;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartIdSpecifier;
import hypertalk.ast.containers.PartNameSpecifier;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.properties.PropertyChangeListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PartsTable implements PropertyChangeListener, Serializable {
private static final long serialVersionUID = 4751323911023854452L;

	private Map<String, Part> idhash;
	private Map<String, Part> namehash;
	
	public PartsTable () {
		idhash = new HashMap<>();
		namehash = new HashMap<>();
	}
    
    public void sendPartOpened () {
        Iterator<String> i = idhash.keySet().iterator();
        while (i.hasNext()) {
            idhash.get(i.next()).partOpened();
        }
    }
    
	public void removePart (PartSpecifier ps) throws PartException {
		removePart(getPart(ps));
	}
	
	public void removePart (Part p) {
		
		try {
			String partId = p.getProperty("id").toString();
			String partName = p.getProperty("name").toString();

			idhash.remove(partId);
			namehash.remove(partName);
		} catch (NoSuchPropertyException e) {
			throw new RuntimeException("All parts must have a name and id");
		}
	}
	
	public void addPart(Part p) throws PartException {
		
		try {			
			String partId = p.getProperty("id").toString();
			String partName = p.getProperty("name").toString();
			
			// Make us a listener to changes of this part's properties
			p.getProperties().addPropertyChangeListener(this);
			
			// Check for duplicate id or name
			if (partExists(new PartIdSpecifier(p.getType(), partId)))
				throw new RuntimeException("Duplicate part id");
			if (partExists(new PartNameSpecifier(p.getType(), partName)))
				throw new PartException("A part with the name " + partName + " already exists");

			idhash.put(partId, p);
			namehash.put(partName, p);
			
		} catch (NoSuchPropertyException e) {
			throw new RuntimeException("All parts must have a name and id");
		}				
	}
	
	public Part getPart (PartSpecifier ps) throws PartException {
		
		if (!partExists(ps))
			throw new PartException(ps + " doesn't exist");
		
		if (ps instanceof PartIdSpecifier)
			return idhash.get(ps.value());
		else if (ps instanceof PartNameSpecifier)
			return namehash.get(ps.value());
		else
			throw new RuntimeException("Unhandled part specifier type");
	}

	public boolean partExists (PartSpecifier ps) {
		if (ps instanceof PartIdSpecifier)
			return idhash.containsKey(ps.value());
		else if (ps instanceof PartNameSpecifier)
			return namehash.containsKey(ps.value());
		else
			throw new RuntimeException("Unhandled part specifier type");
	}
	
	public void propertyChanged (PartSpecifier ps, String property, Value oldValue, Value newValue) {
		
		if (property.equals("name")) {			
			Part part = namehash.get(oldValue.stringValue());
			if (part == null)
				throw new RuntimeException("Unable to change the name of a missing part");
			
			namehash.remove(oldValue.stringValue());
			namehash.put(newValue.stringValue(), part);
		}
	}
	
}
