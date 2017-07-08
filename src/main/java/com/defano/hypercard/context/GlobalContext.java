/*
 * GlobalContext
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/*
 * GlobalContext.java
 * @author matt.defano@gmail.com
 * 
 * This class provides the global runtime context for HyperCard; implemented as
 * a singleton, the class maintains a list of active parts, global variables and
 * provides a stack of local variables used during execution. 
 */

package com.defano.hypercard.context;

import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.CardPart;
import com.defano.hypercard.parts.Part;
import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.*;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.NoSuchPropertyException;
import com.defano.hypertalk.exception.PropertyPermissionException;

import java.util.Stack;
import java.util.Vector;

public class GlobalContext {

    private static GlobalContext instance = new GlobalContext();
    
    private SymbolTable globals;
    private GlobalProperties globalProperties = new GlobalProperties();

    /*
     * In this implementation, when HyperCard sends a system-defined message to a part
     * (i.e., "mouseEnter") the reaction to that message exists in its own thread; therefore
     * the entire local context needs to be specific to each thread.
     */
    private ThreadLocal<Stack<Frame>> stack = new ThreadLocal<>();
    private ThreadLocal<Frame> frame = new ThreadLocal<>();
    private ThreadLocal<PartSpecifier> me = new ThreadLocal<>();

    private GlobalContext () {
        globals = new SymbolTable();
    }

    public static GlobalContext getContext () {
        return instance;
    }

    public void newLocalContext () {
        setFrame(new Frame(new SymbolTable(), new Vector<>(), new Value()));
    }

    public void pushContext () {
        getStack().push(getFrame());
        setFrame(new Frame(new SymbolTable(), new Vector<>(), new Value()));
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
        getCard().getPart(ps).sendMessage(message);
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
            Part destPart = get(d.part().evaluateAsSpecifier());
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
            throw new HtSemanticException("Can't put into that part.");
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

    public Part get (PartSpecifier ps) throws PartException {
        return getCard().getPart(ps);
    }
    
    public Value get (String property, PartSpecifier ps) throws NoSuchPropertyException, PartException, HtSemanticException {
        return getCard().getPart(ps).getProperty(property);
    }

    public void set (String property, PartSpecifier ps, Preposition preposition, Chunk chunk, Value value)
    throws NoSuchPropertyException, PropertyPermissionException, PartException, HtSemanticException
    {
        Value mutable = getCard().getPart(ps).getProperty(property);

        if (chunk != null) {
            mutable = Value.setChunk(mutable, preposition, chunk, value);
        } else {
            mutable = Value.setValue(mutable, preposition, value);
        }

        getCard().getPart(ps).setProperty(property, mutable);
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

    public String getItemDelimiter() {
        return globalProperties.getKnownProperty(GlobalProperties.PROP_ITEMDELIMITER).stringValue();
    }

    public void setGlobalProperty(String property, Value value)
            throws NoSuchPropertyException, PropertyPermissionException, HtSemanticException
    {
        globalProperties.setProperty(property, value);
    }

    public void setSelectedText(Value selectedText) {
        globalProperties.defineProperty(GlobalProperties.PROP_SELECTEDTEXT, selectedText, true);
    }

    public Value getSelectedText() {
        return globalProperties.getKnownProperty(GlobalProperties.PROP_SELECTEDTEXT);
    }

    public Value getGlobalProperty(String property) throws NoSuchPropertyException, HtSemanticException {
        return globalProperties.getProperty(property);
    }

    private Stack<Frame> getStack() {
        if (this.stack.get() == null) {
            this.stack.set(new Stack<>());
        }

        return this.stack.get();
    }

    private void setFrame(Frame frame) {
        this.frame.set(frame);
    }

    private Frame getFrame() {
        return frame.get();
    }
}
