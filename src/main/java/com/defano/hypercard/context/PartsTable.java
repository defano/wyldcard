/*
 * PartsTable
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PartsTable.java
 * @author matt.defano@gmail.com
 * 
 * This class implements a table of HyperCard parts (buttons, fields, etc). Provides
 * methods for adding, removing or modifying the name of existing parts. 
 */

package com.defano.hypercard.context;

import com.defano.hypercard.parts.Part;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.exception.NoSuchPropertyException;

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
}
