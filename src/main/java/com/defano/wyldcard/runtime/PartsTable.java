package com.defano.wyldcard.runtime;

import com.defano.wyldcard.parts.Part;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.hypertalk.exception.NoSuchPropertyException;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class PartsTable<T extends Part> {

    private final ConcurrentHashMap<Integer, T> idhash;         // Mapping of ID to part

    public PartsTable () {
        idhash = new ConcurrentHashMap<>();
    }
    
    public void removePart (T p) {
        
        try {
            int partId = p.getProperty(PartModel.PROP_ID).integerValue();
            idhash.remove(partId);
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("Bug! All parts must have an id.", e);
        }
    }
    
    public void addPart(T p) {
        try {
            int partId = p.getProperty(PartModel.PROP_ID).integerValue();

            // Check for duplicate id or name
            if (idhash.containsKey(partId)) {
                throw new RuntimeException("Bug! Duplicate part id: " + partId);
            }

            idhash.put(partId, p);

        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("Bug! All parts must have an id.", e);
        }                
    }

    public Collection<T> getParts() {
        return idhash.values();
    }

    public T getPart(PartModel model) {
        return idhash.get(model.getId());
    }
}
