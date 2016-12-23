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
import hypercard.parts.model.PropertyChangeObserver;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartIdSpecifier;
import hypertalk.ast.containers.PartNameSpecifier;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.NoSuchPropertyException;

import java.util.*;

public class PartsTable<T extends Part> implements PropertyChangeObserver {

    private Map<Integer, T> idhash;
    private Map<String, T> namehash;
    
    public PartsTable () {
        idhash = new HashMap<>();
        namehash = new HashMap<>();
    }
    
    public void sendPartOpened () {
        for (Integer integer : idhash.keySet()) {
            idhash.get(integer).partOpened();
        }
    }

    public void removePart (T p) {
        
        try {
            Integer partId = p.getProperty("id").integerValue();
            String partName = p.getProperty("name").toString();

            idhash.remove(partId);
            namehash.remove(partName);
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("All parts must have a name and id");
        }
    }
    
    public void addPart(T p) throws PartException {
        
        try {            
            Integer partId = p.getProperty("id").integerValue();
            String partName = p.getProperty("name").toString();
            
            // Make us a listener to changes of this part's model
            p.getPartModel().addPropertyChangedObserver(this);
            
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
    
    public T getPart (PartSpecifier ps) throws PartException {
        
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
    
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        
        if (property.equals("name")) {            
            T part = namehash.get(oldValue.stringValue());
            if (part == null)
                throw new RuntimeException("Unable to change the name of a missing part");
            
            namehash.remove(oldValue.stringValue());
            namehash.put(newValue.stringValue(), part);
        }
    }

    public Collection<T> getParts() {
        return idhash.values();
    }

    public int getNextId () {
        for (int nextId = 0; ; nextId++) {
            if (!idhash.containsKey(nextId)) {
                return nextId;
            }
        }
    }
}
