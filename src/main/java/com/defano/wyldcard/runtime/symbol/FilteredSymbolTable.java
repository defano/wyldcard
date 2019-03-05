package com.defano.wyldcard.runtime.symbol;

import com.defano.hypertalk.ast.model.Value;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A symbol table that reports a subset of symbols based on a filtering list (useful for reporting only in-scope global
 * variables).
 */
public class FilteredSymbolTable implements SymbolTable {

    private final SymbolTable delegate;
    private final Collection<String> visibleSymbols;

    public FilteredSymbolTable(SymbolTable delegate, Collection<String> visibleSymbols) {
        this.delegate = delegate;
        this.visibleSymbols = visibleSymbols;
    }

    @Override
    public Value get(String id) {
        Value v = isSymbolVisible(id) ? delegate.get(id) : null;
        return v == null ? new Value() : v;
    }

    @Override
    public void set(String id, Value v) {
        visibleSymbols.add(id);
        delegate.set(id, v);
    }

    @Override
    public boolean contains(String id) {
        return isSymbolVisible(id) && delegate.contains(id);
    }

    @Override
    public Collection<String> getSymbols() {
        Set<String> symbols = new HashSet<>();
        for (String thisId : delegate.getSymbols()) {
            if (isSymbolVisible(thisId)) {
                symbols.add(thisId);
            }
        }
        return symbols;
    }

    @Override
    public void addObserver(SymbolObserver observer) {
        delegate.addObserver(observer);
    }

    @Override
    public void removeObserver(SymbolObserver observer) {
        delegate.removeObserver(observer);
    }

    private boolean isSymbolVisible(String id) {
        for (String thisSymbol : visibleSymbols) {
            if (thisSymbol.equalsIgnoreCase(id)) {
                return true;
            }
        }

        return false;
    }
}
