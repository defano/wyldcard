package com.defano.wyldcard.runtime;

import com.defano.hypertalk.ast.model.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements a list of symbols (variables) and methods for getting and setting
 * their value.
 */
public class SymbolTable {

    private final Map<String, Value> table;
    
    public SymbolTable () {
        table = new ConcurrentHashMap<>();
    }
    
    public Value get (String id) {
        Value v = table.get(id.toLowerCase());
        if (v == null)
            return new Value();
        return v;
    }
    
    public void put (String id, Value v) {
        table.put(id.toLowerCase(), v);
    }
    
    public boolean exists (String id) {
        return table.containsKey(id.toLowerCase());
    }
}
