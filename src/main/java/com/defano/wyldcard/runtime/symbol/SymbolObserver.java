package com.defano.wyldcard.runtime.symbol;

import com.defano.hypertalk.ast.model.Value;

public interface SymbolObserver {

    /**
     * Invoked to indicate that a symbol in an observed symbol table has been added or modified.
     *
     * @param symbolTable The observed symbol table reporting the change
     * @param id The name of the symbol (variable) that changed
     * @param oldValue The previous value of the variable or null if the variable is being added
     * @param newValue The new (or initial) value of the variable
     */
    void onSymbolChanged(SymbolTable symbolTable, String id, Value oldValue, Value newValue);
}
