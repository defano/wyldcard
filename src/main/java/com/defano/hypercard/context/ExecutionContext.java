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

    // Globals are shared across threads. Race condition? What race condition!?
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
    private final ThreadLocal<Value> result = new ThreadLocal<>();

    private ExecutionContext() {
        globals = new SymbolTable();
    }

    /**
     * Gets the execution context associated with the current thread. Each thread holds a ThreadLocal-reference to
     * its own execution context.
     *
     * @return The ExecutionContext for this thread.
     */
    public static ExecutionContext getContext () {
        return instance;
    }

    /**
     * Pushes a new frame onto the stack.
     */
    public void pushContext () {
        getStack().push(getFrame());
        setFrame(new StackFrame(new SymbolTable(), new Vector<>(), new Value()));
    }

    /**
     * Pops the current frame from the stack.
     */
    public void popContext () {
        setFrame(getStack().pop());
    }

    /**
     * Specifies the value returned from this frame / function / handler. Typically set in response to a 'return xxx'
     * statement in script.
     *
     * @param returnValue The returned value
     */
    public void setReturnValue (Value returnValue) {
        getFrame().setReturnValue(returnValue);
    }

    /**
     * Gets the value returned from this frame.
     * @return The returned value.
     */
    public Value getReturnValue () {
        return getFrame().getReturnValue();
    }

    /**
     * The name of the (last) message passed from this frame. Typically set in response to 'pass xxx'. Used to tell
     * the execution environment that parts down the message passing hierarchy should continue to process the messaage
     * (i.e., the script did not trap the message).
     *
     * @return The name of the passed message.
     */
    public String getPassedMessage() {
        return getFrame().getPassedMessage();
    }

    /**
     * Sets the name of the passed message. See {@link #getPassedMessage()}.
     * @param passedMessage The name of the passed message or null if no message passed.
     */
    public void setPassedMessage(String passedMessage) {
        getFrame().setPassedMessage(passedMessage);
    }

    /**
     * Sets the visual effect to use when next unlocking the screen within the current execution frame.
     * @param visualEffect The visual effect specification
     */
    public void setVisualEffect(VisualEffectSpecifier visualEffect) {
        getFrame().setVisualEffect(visualEffect);
    }

    /**
     * Gets the visual effect to use when next unlocking the screen within the current execution frame.
     * @return The visual effect.
     */
    public VisualEffectSpecifier getVisualEffect() {
        return getFrame().getVisualEffect();
    }

    /**
     * Defines a given id (variable name) as being a global variable in scope for the current frame.
     * @param id The name of the variable to be made global.
     */
    public void defineGlobal (String id) {
        if (!globals.exists(id))
            globals.put(id, new Value());

        getFrame().globalInScope(id);
    }

    /**
     * Sets (assigns) the given symbol (variable) to the given value within the current frame.
     * @param symbol The name of the variable to assign
     * @param v The value to assign it
     */
    public void set (String symbol, Value v) {
        if (globals.exists(symbol) && getFrame().isGlobalInScope(symbol))
            globals.put(symbol, v);
        else
            getFrame().symbols.put(symbol, v);
    }

    /**
     * Gets the value assigned to a symbol (variable). If the variable is an in-scope global, returns the globally
     * assigned value; if the variable is an in-scope local variable, returns its value. If the variable does not
     * exist, then returns the a Value containing the name of the symbol (allows unrecognized symbols to be treated as
     * unquoted string literals, i.e., 'answer hello').
     *
     * @param symbol The symbol/variable whose value should be retrieved.
     * @return The value of the requested symbol.
     */
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

    /**
     * Gets the part model associated with the specified part.
     *
     * @param ps The part specifier
     * @return The part's model
     * @throws PartException Thrown if no such part exists
     */
    public PartModel get(PartSpecifier ps) throws PartException {
        if (ps.isCardElementSpecifier()) {
            return HyperCard.getInstance().getCard().findPart(ps);
        } else if (ps.isStackElementSpecifier()) {
            return HyperCard.getInstance().getStack().findPart(ps);
        }

        throw new IllegalStateException("Bug! Unhandled part type: " + ps);
    }

    /**
     * Gets the value of a property assigned to a given part.
     *
     * @param property The name of the property to retrieve
     * @param ps A part's specifier
     * @return The value of the requested property
     * @throws NoSuchPropertyException Thrown if the property does not exist on the given part
     * @throws PartException Thrown if the part does not exist
     */
    public Value get (String property, PartSpecifier ps) throws NoSuchPropertyException, PartException {
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

    /**
     * Determines if "me" is bound in the current context (it wont be in all contexts, as when entering an expression
     * in the message box, for example).
     *
     * @return True if me is bound; false otherwise
     */
    public boolean hasMe () {
        return me.get() != null;
    }

    /**
     * Sets the value of me in this context.
     * @param me The part referred to as 'me'
     */
    public void setMe (PartSpecifier me) {
        this.me.set(me);
    }

    /**
     * Gets the part referred to as 'me'.
     * @return The part referred to as me.
     * @throws HtSemanticException Thrown if no part is bound to 'me' in this context
     */
    public PartSpecifier getMe () throws HtSemanticException {
        if (me.get() == null) {
            throw new HtSemanticException("Can't refer to 'me' in this context.");
        }

        return this.me.get();
    }

    public void setSelectedText(Value selectedText) {
        globalProperties.defineProperty(HyperCardProperties.PROP_SELECTEDTEXT, selectedText, true);
    }

    /**
     * Gets a set of global properties. That is, properties that apply to HyperCard at large.
     * @return The set of global properties.
     */
    public HyperCardProperties getGlobalProperties() {
        return globalProperties;
    }

    /**
     * Sets the "result" (a special property holding the last produced error message; mutated only by certain commands).
     * @param result An error message
     */
    public void setResult(Value result) {
        this.result.set(result);
    }

    /**
     * Gets the "result" (a special property holding the last produced error message; mutated only by certain commands).
     * @return The result; may be the empty string to denote no error.
     */
    public Value getResult() {
        return this.result.get() == null ? new Value() : this.result.get();
    }

    /**
     * Sends a message to a part on the current card.
     *
     * @param ps A specifier identifying the part
     * @param message The message to be sent
     * @throws PartException Thrown if the specified part does not exist
     */
    public void sendMessage (PartSpecifier ps, String message) throws PartException
    {
        HyperCard.getInstance().getCard().findPart(ps).sendMessage(message);
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
