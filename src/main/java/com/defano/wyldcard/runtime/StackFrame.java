package com.defano.wyldcard.runtime;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;

import java.util.ArrayList;
import java.util.List;

public class StackFrame {

    private final SymbolTable symbols;              // Local variables
    private final List<String> globalsInScope;      // Global variables that are in scope in this frame

    private long creationTime;                      // Time when this frame was created
    private List<Value> params = new ArrayList<>(); // Arguments passed to this function/handler
    private String message = "";                    // The name of this function/handler
    private String passedMessage;                   // Name of the message passed (via 'pass' command)
    private VisualEffectSpecifier visualEffect;     // Visual effect to use to unlock screen
    private Value returnValue;                      // Value returned from this function
    
    public StackFrame() {
        this.creationTime = System.currentTimeMillis();
        this.symbols = new SymbolTable();
        this.globalsInScope = new ArrayList<>();
        this.returnValue = new Value();
    }

    public long getCreationTimeMs() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public SymbolTable getSymbols() {
        return symbols;
    }

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

    public void globalInScope (String symbol) {
        globalsInScope.add(symbol);
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
