package com.defano.wyldcard.runtime.symbol;

import com.defano.hypertalk.ast.model.Value;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A symbol table composed of one or more symbol tables; useful for joining local and global variable symbol tables.
 */
public class CompositeSymbolTable implements SymbolTable {

    private final SymbolTable[] symbolTables;

    public CompositeSymbolTable(SymbolTable... symbolTables) {
        this.symbolTables = symbolTables;
    }

    @Override
    public Value get(String id) {
        for (SymbolTable thisTable : symbolTables) {
            if (thisTable.contains(id)) {
                return thisTable.get(id);
            }
        }

        return new Value();
    }

    @Override
    public void set(String id, Value v) {
        for (SymbolTable thisTable : symbolTables) {
            if (thisTable.contains(id)) {
                thisTable.set(id, v);
            }
        }
    }

    @Override
    public boolean contains(String id) {
        for (SymbolTable thisTable : symbolTables) {
            if (thisTable.contains(id)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Collection<String> getSymbols() {
        Set<String> symbols = new HashSet<>();
        for (SymbolTable thisTable : symbolTables) {
            symbols.addAll(thisTable.getSymbols());
        }
        return symbols;
    }

    @Override
    public void addObserver(SymbolObserver observer) {
        for (SymbolTable thisTable : symbolTables) {
            thisTable.addObserver(observer);
        }
    }

    @Override
    public void removeObserver(SymbolObserver observer) {
        for (SymbolTable thisTable : symbolTables) {
            thisTable.removeObserver(observer);
        }
    }
}
