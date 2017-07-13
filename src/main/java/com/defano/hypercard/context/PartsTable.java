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
import com.defano.hypercard.parts.ZOrderComparator;
import com.defano.hypertalk.ast.common.PartLayer;
import com.defano.hypertalk.ast.containers.PartIdSpecifier;
import com.defano.hypertalk.ast.containers.PartNameSpecifier;
import com.defano.hypertalk.ast.containers.PartNumberSpecifier;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.NoSuchPropertyException;

import java.util.*;
import java.util.stream.Collectors;

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
            if (partExists(new PartIdSpecifier(null, p.getType(), partId)))
                throw new RuntimeException("Duplicate part id: " + partId);

            idhash.put(partId, p);

        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("All parts must have a valid name and id");
        }                
    }

    public T getPart (PartSpecifier ps) throws PartException {

        T foundPart = getPartOrNull(ps);

        if (foundPart == null || (ps.layer() != null && foundPart.getCardLayer().asPartLayer() != ps.layer())) {
            throw new PartException("Sorry, " + ps.toString().toLowerCase() + " doesn't exist.");
        }

        return foundPart;
    }
    
    private T getPartOrNull (PartSpecifier ps) {

        try {
            if (ps instanceof PartIdSpecifier) {
                return idhash.get(((PartIdSpecifier) ps).value());
            } else if (ps instanceof PartNameSpecifier) {
                return partByName(String.valueOf(ps.value()));
            } else if (ps instanceof PartNumberSpecifier) {
                List<T> parts = new ArrayList<>(getParts());
                parts.sort(new ZOrderComparator());
                return parts.get(((PartNumberSpecifier) ps).number - 1);
            }
        } catch (Throwable t) {
            return null;
        }

        return null;
    }

    public boolean partExists (PartSpecifier ps) {

        if (ps instanceof PartIdSpecifier) {
            return idhash.containsKey(((PartIdSpecifier) ps).value());
        }

        else if (ps instanceof PartNumberSpecifier) {
            return idhash.keySet().size() >= ((PartNumberSpecifier)ps).number;
        }

        else if (ps instanceof PartNameSpecifier) {
            return partByName(String.valueOf(ps.value())) != null;
        }

        else {
            throw new RuntimeException("Unhandled part specifier type");
        }
    }

    public Collection<T> getPart(PartLayer layer) {
        return idhash.values()
                .stream()
                .filter(p -> p.getCardLayer().asPartLayer() == layer)
                .collect(Collectors.toList());
    }

    public Collection<T> getParts() {
        return idhash.values();
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
