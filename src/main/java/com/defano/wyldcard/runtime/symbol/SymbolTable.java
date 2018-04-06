package com.defano.wyldcard.runtime.symbol;

import com.defano.hypertalk.ast.model.Value;

import java.util.Collection;

public interface SymbolTable {

    Value get(String id);
    void set(String id, Value v);
    boolean exists(String id);
    Collection<String> getSymbols();

    void addObserver(SymbolObserver observer);
    void removeObserver(SymbolObserver observer);
}
