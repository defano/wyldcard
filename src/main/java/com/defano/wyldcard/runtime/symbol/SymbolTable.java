package com.defano.wyldcard.runtime.symbol;

import com.defano.hypertalk.ast.model.Value;

import java.util.Collection;

public interface SymbolTable {

    /**
     * Gets the value of the specified symbol. Returns an empty {@link Value} if no such symbol exists in this symbol
     * table.
     *
     * @param id The symbol to retrieve; id is case insensitive.
     * @return The value assigned to this symbolic name or an empty value if no value was assigned.
     */
    Value get(String id);

    /**
     * Sets the value of the specified symbol.
     *
     * @param id The name of the symbol to be set; if the symbol already exists in the table its value will be updated,
     *           if this is a new symbol, it will be created with the specified value. Note that symbol ids (names) are
     *           case insensitive.
     * @param v
     */
    void set(String id, Value v);

    /**
     * Determines if a symbol of the specified name already exists in the table.
     *
     * @param id The name of the symbol to check; case insensitive.
     * @return True if the symbol has been defined in the table (via a prior call to {@link #set(String, Value)}; false
     * otherwise.
     */
    boolean contains(String id);

    /**
     * Gets an unordered collection of all symbols defined in this table.
     * @return All symbols in this table.
     */
    Collection<String> getSymbols();

    /**
     * Adds an observer of this symbol table. Observer will be notified of symbol additions and changes.
     * @param observer The observer
     */
    void addObserver(SymbolObserver observer);

    /**
     * Removes an observer of this symbol table; has no effect if the observer does not exist.
     * @param observer The observer
     */
    void removeObserver(SymbolObserver observer);
}
