package com.defano.wyldcard.runtime.symbol;

import com.defano.hypertalk.ast.model.Value;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements a list of symbols (variables) and methods for getting, setting and observing their values.
 */
public class BasicSymbolTable implements SymbolTable {

    private final Map<String, Value> table;
    private final List<SymbolObserver> observers = new ArrayList<>();
    
    public BasicSymbolTable() {
        table = new ConcurrentHashMap<>();
    }

    @Override
    public Value get (String id) {
        Value v = table.get(id.toLowerCase());
        if (v == null)
            return new Value();
        return v;
    }

    @Override
    public void set(String id, Value v) {
        Value oldValue = exists(id) ? get(id) : null;
        table.put(id.toLowerCase(), v);

        fireObservers(id, oldValue, v);
    }

    @Override
    public boolean exists (String id) {
        return table.containsKey(id.toLowerCase());
    }

    @Override
    public Collection<String> getSymbols() {
        return table.keySet();
    }

    @Override
    public void addObserver(SymbolObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(SymbolObserver observer) {
        observers.remove(observer);
    }

    private void fireObservers(String id, Value oldValue, Value newValue) {
        if (!observers.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                for (SymbolObserver thisObserver : observers) {
                    thisObserver.onSymbolChanged(BasicSymbolTable.this, id, oldValue, newValue);
                }
            });
        }
    }
}
