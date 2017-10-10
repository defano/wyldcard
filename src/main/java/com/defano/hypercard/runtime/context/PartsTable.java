package com.defano.hypercard.runtime.context;

import com.defano.hypercard.parts.Part;
import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.exception.NoSuchPropertyException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PartsTable<T extends Part> {

    private final Map<Integer, T> idhash;         // Mapping of ID to part

    public PartsTable () {
        idhash = new HashMap<>();
    }
    
    public void removePart (T p) {
        
        try {
            Integer partId = p.getProperty(PartModel.PROP_ID).integerValue();
            idhash.remove(partId);
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("All parts must have a valid name and id");
        }
    }
    
    public void addPart(T p) throws PartException {
        
        try {            
            Integer partId = p.getProperty(PartModel.PROP_ID).integerValue();

            // Check for duplicate id or name
            if (idhash.containsKey(partId)) {
                throw new RuntimeException("Bug! Attempt to add part with existing part id: " + partId);
            }

            idhash.put(partId, p);

        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("All parts must have a valid name and id");
        }                
    }

    public Collection<T> getParts() {
        return idhash.values();
    }

    public T getPartForModel(PartModel model) {
        return idhash.get(model.getId());
    }
}
