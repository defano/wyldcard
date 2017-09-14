/*
 * Frame
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * StackFrame.java
 * @author matt.defano@gmail.com
 * 
 * Maintains the current local context; analagous to the current stack frame.
 */

package com.defano.hypercard.runtime.context;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;

import java.util.ArrayList;
import java.util.List;

public class StackFrame {

    public final SymbolTable symbols;
    private final List<String> globalsInScope;
    private List<Value> params = new ArrayList<>();
    private String message = "";

    private String passedMessage;
    private VisualEffectSpecifier visualEffect;
    private Value returnValue;
    
    public StackFrame(SymbolTable symbols, List<String> globalsInScope, Value returnValue) {
        this.symbols = symbols;
        this.globalsInScope = globalsInScope;
        this.returnValue = returnValue;
        
        // "it" is implemented as a global variable that's always in scope
        globalsInScope.add("it");
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
