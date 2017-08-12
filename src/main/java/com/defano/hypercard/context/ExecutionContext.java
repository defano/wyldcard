/*
 * GlobalContext
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.context;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;
import com.defano.hypertalk.ast.containers.*;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.NoSuchPropertyException;
import com.defano.hypertalk.exception.PropertyPermissionException;

import java.util.Stack;
import java.util.Vector;

public class ExecutionContext {

    private static final ExecutionContext instance = new ExecutionContext();

    // Globals are shared across threads. Race conditions, pfft, what race condition?
    private final SymbolTable globals;
    private final HyperCardProperties globalProperties = new HyperCardProperties();

    /*
     * In this implementation, when HyperCard sends a system-defined message to a part
     * (i.e., "mouseEnter") the reaction to that message exists in its own thread; therefore
     * the entire local context needs to be specific to each thread.
     */
    private final ThreadLocal<Stack<StackFrame>> stack = new ThreadLocal<>();
    private final ThreadLocal<StackFrame> frame = new ThreadLocal<>();
    private final ThreadLocal<PartSpecifier> me = new ThreadLocal<>();

    private ExecutionContext() {
        globals = new SymbolTable();
    }

    public static ExecutionContext getContext () {
        return instance;
    }

    public void newLocalContext () {
        setFrame(new StackFrame(new SymbolTable(), new Vector<>(), new Value()));
    }

    public void pushContext () {
        getStack().push(getFrame());
        setFrame(new StackFrame(new SymbolTable(), new Vector<>(), new Value()));
    }

    public void popContext () {
        setFrame(getStack().pop());
    }

    public void setReturnValue (Value returnValue) {
        getFrame().setReturnValue(returnValue);
    }

    public Value getReturnValue () {
        return getFrame().getReturnValue();
    }

    public String getPassedMessage() {
        return getFrame().getPassedMessage();
    }

    public void setPassedMessage(String passedMessage) {
        getFrame().setPassedMessage(passedMessage);
    }

    public void setVisualEffect(VisualEffectSpecifier visualEffect) {
        getFrame().setVisualEffect(visualEffect);
    }

    public VisualEffectSpecifier getVisualEffect() {
        return getFrame().getVisualEffect();
    }

    public void defineGlobal (String id) {
        if (!globals.exists(id))
            globals.put(id, new Value());

        getFrame().globalInScope(id);
    }

    public void set (String symbol, Value v) {
        if (globals.exists(symbol) && getFrame().isGlobalInScope(symbol))
            globals.put(symbol, v);
        else
            getFrame().symbols.put(symbol, v);
    }

    public Value get (String symbol) {
        Value value;

        if (globals.exists(symbol) && getFrame().isGlobalInScope(symbol))
            value = globals.get(symbol);
        else if (getFrame().symbols.exists(symbol))
            value = getFrame().symbols.get(symbol);

        // Allow the user to refer to literals without quotation marks
        else
            value = new Value(symbol);

        return value;
    }

    public CardPart getCard () {
        return HyperCard.getInstance().getCard();
    }

    public void sendMessage (PartSpecifier ps, String message)
    throws HtSemanticException, PartException
    {
        getCard().findPart(ps).sendMessage(message);
    }
    
    public void put (Value mutator, Preposition p, ContainerMsgBox d) throws HtSemanticException {

        Chunk chunk = d.chunk();
        Value destValue = new Value(HyperCard.getInstance().getMessageBoxText());
        
        // Operating on a chunk of the existing value
        if (chunk != null)
            destValue = Value.setChunk(destValue, p, chunk, mutator);
        else
            destValue = Value.setValue(destValue, p, mutator);
        
        HyperCard.getInstance().setMessageBoxText(destValue);
        setIt(destValue);
    }
    
    public void put (Value mutator, Preposition p, ContainerPart d) throws HtSemanticException {
        
        try {
            PartModel destPart = get(d.part().evaluateAsSpecifier());
            Value destValue = destPart.getValue();
            Chunk chunk = d.chunk();
            
            // Operating on a chunk of the existing value
            if (chunk != null)
                destValue = Value.setChunk(destValue, p, chunk, mutator);
            else
                destValue = Value.setValue(destValue, p, mutator);
        
            destPart.setValue(destValue);
            setIt(destValue);
            
        } catch (PartException e) {
            throw new HtSemanticException("Can't put into that part.", e);
        }
    }
    
    public void put (Value mutator, Preposition p, ContainerVariable d) throws HtSemanticException {
        String symbol = d.symbol();
        Chunk chunk = d.chunk();
        Value mutable = get(symbol);
        
        // Operating on a chunk of the existing value
        if (chunk != null)
            mutable = Value.setChunk(mutable, p, chunk, mutator);
        else
            mutable = Value.setValue(mutable, p, mutator);
        
        set(symbol, mutable);
        setIt(mutable);
    }

    public PartModel get(PartSpecifier ps) throws PartException {
        if (ps.isCardElementSpecifier()) {
            return getCard().findPart(ps);
        } else if (ps.isStackElementSpecifier()) {
            return HyperCard.getInstance().getStack().findPart(ps);
        }

        throw new IllegalStateException("Bug! Unhandled part type: " + ps);
    }

    public Value get (String property, PartSpecifier ps) throws NoSuchPropertyException, PartException, HtSemanticException {
        return get(ps).getProperty(property);
    }

    public void set (String property, PartSpecifier ps, Preposition preposition, Chunk chunk, Value value)
    throws NoSuchPropertyException, PropertyPermissionException, PartException, HtSemanticException
    {
        Value mutable = get(ps).getProperty(property);

        if (chunk != null) {
            mutable = Value.setChunk(mutable, preposition, chunk, value);
        } else {
            mutable = Value.setValue(mutable, preposition, value);
        }

        get(ps).setProperty(property, mutable);
    }
    
    public void setIt (Object value) {
        globals.put("it", new Value(value.toString()));
    }
    
    public Value getIt () {
        return globals.get("it");
    }

    public void setMe (PartSpecifier me) {
        this.me.set(me);
    }

    public PartSpecifier getMe () {
        return this.me.get();
    }

    public void setSelectedText(Value selectedText) {
        globalProperties.defineProperty(HyperCardProperties.PROP_SELECTEDTEXT, selectedText, true);
    }

    public HyperCardProperties getGlobalProperties() {
        return globalProperties;
    }

    private Stack<StackFrame> getStack() {
        if (this.stack.get() == null) {
            this.stack.set(new Stack<>());
        }

        return this.stack.get();
    }

    private void setFrame(StackFrame stackFrame) {
        this.frame.set(stackFrame);
    }

    private StackFrame getFrame() {
        return frame.get();
    }
}
