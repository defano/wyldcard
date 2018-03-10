package com.defano.hypercard.runtime.context;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.stack.StackPart;
import com.defano.hypercard.runtime.HyperCardProperties;
import com.defano.hypercard.runtime.StackFrame;
import com.defano.hypercard.runtime.SymbolTable;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.Chunk;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.NoSuchPropertyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Represents the state of HyperCard during the execution of a HyperTalk script.
 *
 * This object maintains the call stack, local and global variables, the part referred to as 'me', any pending visual
 * effect, and any return value passed out of a function handler.
 *
 * A note about threading: When HyperCard sends a message to a part (like 'mouseDown') the handler associated with that
 * message executes in its own thread. Any messages sent or functions invoked from that system message handler execute
 * in the same thread. Thus, each system message produces its own thread.
 */
public class ExecutionContext {

    private static final ExecutionContext instance = new ExecutionContext();

    // Globals are shared across threads. SymbolTable is thread safe.
    private final SymbolTable globals = new SymbolTable();

    /*
     * In this implementation, when HyperCard sends a system-defined message to a part
     * (i.e., "mouseEnter") the reaction to that message exists in its own thread; therefore
     * the entire local context needs to be specific to each thread.
     */
    private final ThreadLocal<Stack<StackFrame>> stack = new ThreadLocal<>();
    private final ThreadLocal<StackFrame> frame = new ThreadLocal<>();
    private final ThreadLocal<Stack<PartSpecifier>> me = new ThreadLocal<>();
    private final ThreadLocal<Value> result = new ThreadLocal<>();
    private final ThreadLocal<CardPart> card = new ThreadLocal<>();
    private final ThreadLocal<PartSpecifier> theTarget = new ThreadLocal<>();

    private ExecutionContext() {}

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
        setFrame(new StackFrame(new SymbolTable(), new ArrayList<>(), new Value()));
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
     * the execution environment that parts down the message passing hierarchy should continue to process the message
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
    public void setVariable(String symbol, Value v) {
        if (globals.exists(symbol) && getFrame().isGlobalInScope(symbol))
            globals.put(symbol, v);
        else
            getFrame().getSymbols().put(symbol, v);
    }

    public void setVariable(String symbol, Preposition preposition, Chunk chunk, Value value) throws HtException {

        // When mutating the value of an un-scoped symbol, do not resolve the value of that symbol to be the symbols's
        // name itself.
        Value mutable = isVariableInScope(symbol) ? getVariable(symbol) : new Value();

        // Operating on a chunk of the existing value
        if (chunk != null)
            mutable = Value.setChunk(mutable, preposition, chunk, value);
        else
            mutable = Value.setValue(mutable, preposition, value);

        ExecutionContext.getContext().setVariable(symbol, mutable);
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
    public Value getVariable(String symbol) {
        Value value;

        if (globals.exists(symbol) && getFrame().isGlobalInScope(symbol))
            value = globals.get(symbol);
        else if (getFrame().getSymbols().exists(symbol))
            value = getFrame().getSymbols().get(symbol);

        // Allow the user to refer to literals without quotation marks
        else
            value = new Value(symbol);

        return value;
    }

    /**
     * Determines if the given symbol name refers to an in-scope variable (local or global).
     * @param symbol The symbol (variable name) to test
     * @return True if the symbol is an in-scope variable, false otherwise
     */
    public boolean isVariableInScope(String symbol) {
        return globals.exists(symbol) && getFrame().isGlobalInScope(symbol) || getFrame().getSymbols().exists(symbol);
    }

    /**
     * Gets the part model associated with the specified part.
     *
     * @param ps The part specifier
     * @return The part's model
     * @throws PartException Thrown if no such part exists
     */
    public PartModel getPart(PartSpecifier ps) throws PartException {
        return HyperCard.getInstance().getActiveStack().getStackModel().findPart(ps);
    }

    /**
     * Gets the value of a property assigned to a given part.
     *
     * @param property The name of the property to retrieve
     * @param ps A part's specifier, or null to indicate a HyperCard property
     * @return The value of the requested property
     * @throws NoSuchPropertyException Thrown if the property does not exist on the given part
     * @throws PartException Thrown if the part does not exist
     */
    public Value getProperty(String property, PartSpecifier ps) throws NoSuchPropertyException, PartException {
        if (ps == null) {
            return HyperCardProperties.getInstance().getProperty(property);
        } else {
            return getPart(ps).getProperty(property);
        }
    }

    /**
     * Sets the value of a property assigned to a given part.
     *
     * @param property The name of the property to set
     * @param ps The PartSpecifier identifying the part, or null to specify a HyperCard property
     * @param preposition A preposition indicating where to place the value
     * @param chunk When non-null, indicates that a chunk of the property should be mutated
     * @param value The value to place into the property
     * @throws HtException Thrown if an error occurs setting the property
     */
    public void setProperty(String property, PartSpecifier ps, Preposition preposition, Chunk chunk, Value value) throws HtException
    {
        Value mutable = getProperty(property, ps);

        if (chunk != null) {
            mutable = Value.setChunk(mutable, preposition, chunk, value);
        } else {
            mutable = Value.setValue(mutable, preposition, value);
        }

        if (ps == null) {
            HyperCardProperties.getInstance().setProperty(property, mutable);
        } else {
            getPart(ps).setProperty(property, mutable);
        }
    }

    /**
     * Sets the card context in which the current script is executing. That is, when a script calls for "card field 1"
     * the script is referring to the first card on the CardPart passed to this method.
     *
     * This is typically the same card as the one being displayed, but can vary during sort and find commands.
     *
     * @param card The card representing the context in which the current script is executing.
     */
    public void setCurrentCard(CardPart card) {
        this.card.set(card);
    }

    /**
     * Returns the card in scope of this execution context. That is, the card that the currently executing script should
     * interrogate when looking for parts and properties.
     *
     * In most cases, this method returns the card visible to the user (not accounting for screen lock; equivalent to
     * {@link HyperCard#getActiveStackDisplayedCard()} but during certain operations (like card sorting) this method may return a
     * different value.
     *
     * In general, scripts should always use this method for getting a reference to the active card; UI elements (like
     * menus and palettes) should use {@link HyperCard#getActiveStackDisplayedCard()}.
     *
     * @return The active card in the context of this script execution.
     */
    public CardPart getCurrentCard() {
        CardPart currentCard = this.card.get();
        if (currentCard == null) {
            return HyperCard.getInstance().getActiveStackDisplayedCard();
        } else {
            return currentCard;
        }
    }

    public StackPart getCurrentStack() {
        return HyperCard.getInstance().getActiveStack();
    }

    /**
     * Sets the current value of the implicit 'it' variable in this context.
     * @param value The value of 'it'.
     */
    public void setIt (Object value) {
        setVariable("it", new Value(value));
    }

    /**
     * Gets the current value of the implicit variable 'it' in this context.
     * @return The value of 'it'.
     */
    public Value getIt () {
        return getVariable("it");
    }

    /**
     * Sets the message (i.e., the name of the handler or function) that was invoked to cause this script to execute.
     *
     * @param message The message name invoked.
     */
    public void setMessage(String message) {
        this.getFrame().setMessage(message);
    }

    /**
     * Gets the message (i.e., the name of the handler or function) that was invoked to cause this script to execute.
     *
     * @return The name of message.
     */
    public String getMessage() {
        return getFrame().getMessage();
    }

    public void setParams(List<Value> params) {
        this.getFrame().setParams(params);
    }

    /**
     * Gets the arguments passed to this handler or function. Does not return the parameter names, but is named this
     * way to correspond with HyperTalk's 'the params' function (which also ought to be called 'the args', but alas).
     *
     * @return A list of arguments passed to this handler in the order they were passed.
     */
    public List<Value> getParams() {
        return this.getFrame().getParams();
    }

    /**
     * Determines if "me" is bound in the current context.
     *
     * @return True if me is bound; false otherwise
     */
    public boolean hasMe () {
        return me.get() != null;
    }

    /**
     * Sets the value of me in this context.
     *
     * @param me The part referred to as 'me'
     */
    public void pushMe(PartSpecifier me) {
        getMeStack().push(me);
    }

    public PartSpecifier popMe() {
        return getMeStack().pop();
    }

    /**
     * Gets a specifier referencing the part referred to as 'me'.
     *
     * @return The part referred to as me.
     * @throws HtSemanticException Thrown if no part is bound to 'me' in this context
     */
    public PartSpecifier getMe () throws HtSemanticException {
        if (getMeStack().size() == 0) {
            throw new HtSemanticException("Can't refer to 'me' in this context.");
        }

        return getMeStack().peek();
    }

    /**
     * Gets a set of global properties. That is, properties that apply to HyperCard at large.
     * @return The set of global properties.
     */
    public HyperCardProperties getGlobalProperties() {
        return HyperCardProperties.getInstance();
    }

    /**
     * Sets "the result" (a special property holding the last produced error message; mutated only by certain commands).
     * @param result An error message
     */
    public void setResult(Value result) {
        this.result.set(result);
    }

    /**
     * Gets "the result" (a special property holding the last produced error message; mutated only by certain commands).
     *
     * @return The result; may be the empty string to denote no error.
     */
    public Value getResult() {
        return this.result.get() == null ? new Value() : this.result.get();
    }

    /**
     * Gets "the target", that is, the first object in the message passing hierarchy that received the message.
     * @return A PartSpecifier representing the target
     */
    public PartSpecifier getTarget() {
        return theTarget.get();
    }

    /**
     * Sets "the target", that is, the first object in the message passing hierarchy that received the message.
     * @param theTarget The target PartSpecifier
     */
    public void setTarget(PartSpecifier theTarget) {
        this.theTarget.set(theTarget);
    }

    /**
     * Sends a message to a part on the current card.
     *
     * @param ps A specifier identifying the part
     * @param message The message to be sent
     * @param arguments Message arguments
     * @throws PartException Thrown if the specified part does not exist
     */
    public void sendMessage (PartSpecifier ps, String message, ListExp arguments) throws PartException
    {
        PartModel thePart = getPart(ps);

        if (thePart != null) {
            thePart.receiveMessage(message, arguments);
        } else {
            throw new PartException("No such part.");
        }
    }

    private Stack<PartSpecifier> getMeStack() {
        if (this.me.get() == null) {
            this.me.set(new Stack<>());
        }

        return this.me.get();
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

    public StackFrame getFrame() {
        return frame.get();
    }
}
