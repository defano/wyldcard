package com.defano.hypercard.runtime;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;

import java.util.ArrayList;
import java.util.List;

public class StackFrame {

    private final long creationTime;
    private final SymbolTable symbols;
    private final List<String> globalsInScope;

    private List<Value> params = new ArrayList<>();
    private String message = "";
    private String passedMessage;
    private VisualEffectSpecifier visualEffect;
    private Value returnValue;
    
    public StackFrame(SymbolTable symbols, List<String> globalsInScope, Value returnValue) {
        this.creationTime = System.currentTimeMillis();
        this.symbols = symbols;
        this.globalsInScope = globalsInScope;
        this.returnValue = returnValue;
    }

    public long getCreationTimeMs() {
        return creationTime;
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
