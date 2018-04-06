package com.defano.wyldcard.runtime;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.symbol.BasicSymbolTable;
import com.defano.wyldcard.runtime.symbol.CompositeSymbolTable;
import com.defano.wyldcard.runtime.symbol.FilteredSymbolTable;
import com.defano.wyldcard.runtime.symbol.SymbolTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StackFrame {

    private final SymbolTable localVariables;       // Local variables
    private final List<String> globalsInScope;      // Global variables that are in scope in this frame

    private long creationTime;                      // Time when this frame was created
    private List<Value> params = new ArrayList<>(); // Arguments passed to this function/handler
    private String message = "";                    // The name of this function/handler
    private String passedMessage;                   // Name of the message passed (via 'pass' command)
    private VisualEffectSpecifier visualEffect;     // Visual effect to use to unlock screen
    private Value returnValue;                      // Value returned from this function
    
    public StackFrame() {
        this.creationTime = System.currentTimeMillis();
        this.localVariables = new BasicSymbolTable();
        this.globalsInScope = new ArrayList<>();
        this.returnValue = new Value();
    }

    /**
     * Gets the time when this stack frame was created (used for determining if certain events occurred while this
     * handler was executing.
     *
     * @return The value of {@link System#currentTimeMillis()} at the moment this frame was created.
     */
    public long getCreationTimeMs() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * Gets the local variables in scope for this call stack frame.
     * @return In-scope local variables
     */
    public SymbolTable getLocalVariables() {
        return localVariables;
    }

    /**
     *
     * @return
     */
    public SymbolTable getScopedGlobalVariables() {
        return new FilteredSymbolTable(ExecutionContext.getGlobals(), globalsInScope);
    }

    /**
     * Gets a read-only SymbolTable (variables cannot be modified) containing all local and in-scope global variables.
     * @return A read-only table of all visible variables.
     */
    public SymbolTable getVariables() {
        return new CompositeSymbolTable(getScopedGlobalVariables(), getLocalVariables());
    }

    /**
     * Gets the message (i.e., the handler or function) being processed by this frame.
     * @return The name of the message being handled by this frame.
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setParams(List<Value> params) {
        this.params = params;
    }

    public List<Value> getParams() {
        return this.params;
    }

    public void setGlobalInScope(String symbol) {
        globalsInScope.add(symbol);
    }

    public Collection<String> getGlobalsInScope() {
        return globalsInScope;
    }

    public boolean isGlobalInScope (String symbol) {
        return globalsInScope.contains(symbol);
    }

    public void setReturnValue (Value returnValue) {
        this.returnValue = returnValue;
    }
    
    public Value getReturnValue () {
        return returnValue;
    }

    public String getPassedMessage() {
        return passedMessage;
    }

    public void setPassedMessage(String passedMessage) {
        this.passedMessage = passedMessage;
    }

    public VisualEffectSpecifier getVisualEffect() {
        return visualEffect;
    }

    public void setVisualEffect(VisualEffectSpecifier visualEffect) {
        this.visualEffect = visualEffect;
    }
}
