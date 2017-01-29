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
import hypertalk.ast.containers.PartIdSpecifier;
import hypertalk.ast.containers.PartNameSpecifier;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.NoSuchPropertyException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PartsTable<T extends Part> {

    private Map<Integer, T> idhash;         // Mapping of ID to part

    public PartsTable () {
        idhash = new HashMap<>();
    }
    
    public void removePart (T p) {
        
        try {
            Integer partId = p.getProperty("id").integerValue();
            idhash.remove(partId);
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("All parts must have a valid name and id");
        }
    }
    
    public void addPart(T p) throws PartException {
        
        try {            
            Integer partId = p.getProperty("id").integerValue();

            // Check for duplicate id or name
            if (partExists(new PartIdSpecifier(p.getType(), partId)))
                throw new RuntimeException("Duplicate part id");

            idhash.put(partId, p);

        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("All parts must have a valid name and id");
        }                
    }
    
    public T getPart (PartSpecifier ps) throws PartException {
        
        if (!partExists(ps))
            throw new PartException(ps + " doesn't exist");
        if (ps instanceof PartIdSpecifier)
            return idhash.get(ps.value());
        else if (ps instanceof PartNameSpecifier)
            return partByName(String.valueOf(ps.value()));
        else
            throw new RuntimeException("Unhandled part specifier type");
    }

    public boolean partExists (PartSpecifier ps) {
        if (ps instanceof PartIdSpecifier)
            return idhash.containsKey(ps.value());
        else if (ps instanceof PartNameSpecifier)
            return partByName(String.valueOf(ps.value())) != null;
        else
            throw new RuntimeException("Unhandled part specifier type");
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

    private T partByName(String name) {
        for (T thisPart : idhash.values()) {
            if (thisPart.getName().equalsIgnoreCase(name)) {
                return thisPart;
            }
        }

        return null;
    }
}
