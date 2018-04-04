package com.defano.wyldcard.runtime.context;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.Chunk;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.NoSuchPropertyException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.awt.KeyboardManager;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.HyperCardProperties;
import com.defano.wyldcard.runtime.StackFrame;
import com.defano.wyldcard.runtime.SymbolTable;

import java.util.List;
import java.util.Stack;

/**
 * Represents the state of HyperCard during the execution of a HyperTalk script.
 * <p>
 * This object maintains the call stack, local and global variables, the part referred to as 'me', any pending visual
 * effect, and any return value passed out of a function handler.
 * <p>
 * A note about threading: When HyperCard sends a message to a part (like 'mouseDown') the handler associated with that
 * message executes in its own thread. Any messages sent or functions invoked from that system message handler execute
 * in the same thread. Thus, each system message effectively produces its own thread (albeit threads are pooled and
 * the number of concurrent script threads are limited).
 */
public class ExecutionContext {

    // Globals are shared across contexts; SymbolTable is thread safe.
    private final SymbolTable globals = new SymbolTable();

    private Stack<StackFrame> stack;        // Call stack
    private Stack<PartSpecifier> meStack;   // Part that the 'me' keyword refers
    private Value result;                   // Value returned by 'the result'
    private CardPart card;                  // "Current" card in the context of this execution
    private PartSpecifier theTarget;        // Part that the message was initially sent

    public ExecutionContext() {
        this.stack = new Stack<>();
        this.meStack = new Stack<>();

        pushStackFrame();
    }

    /**
     * Pushes a new frame onto the stack.
     */
    public void pushStackFrame() {
        stack.push(new StackFrame());
    }

    /**
     * Pops the current frame from the stack.
     */
    public void popStackFrame() {
        stack.pop();
    }

    /**
     * Gets the current stack frame.
     * @return The current stack frame
     */
    public StackFrame getStackFrame() {
        return stack.peek();
    }

    /**
     * Gets the value returned from this frame.
     *
     * @return The returned value.
     */
    public Value getReturnValue() {
        return getStackFrame().getReturnValue();
    }

    /**
     * Specifies the value returned from this frame / function / handler. Typically set in response to a 'return xxx'
     * statement in script.
     *
     * @param returnValue The returned value
     */
    public void setReturnValue(Value returnValue) {
        getStackFrame().setReturnValue(returnValue);
    }

    /**
     * The name of the (last) message passed from this frame. Typically set in response to 'pass xxx'. Used to tell
     * the execution environment that parts down the message passing hierarchy should continue to process the message
     * (i.e., the script did not trap the message).
     *
     * @return The name of the passed message.
     */
    public String getPassedMessage() {
        return getStackFrame().getPassedMessage();
    }

    /**
     * Sets the name of the passed message. See {@link #getPassedMessage()}.
     *
     * @param passedMessage The name of the passed message or null if no message passed.
     */
    public void setPassedMessage(String passedMessage) {
        getStackFrame().setPassedMessage(passedMessage);
    }

    /**
     * Gets the visual effect to use when next unlocking the screen within the current execution frame.
     *
     * @return The visual effect.
     */
    public VisualEffectSpecifier getVisualEffect() {
        return getStackFrame().getVisualEffect();
    }

    /**
     * Sets the visual effect to use when next unlocking the screen within the current execution frame.
     *
     * @param visualEffect The visual effect specification
     */
    public void setVisualEffect(VisualEffectSpecifier visualEffect) {
        getStackFrame().setVisualEffect(visualEffect);
    }

    /**
     * Defines a given id (variable name) as being a global variable in scope for the current frame.
     *
     * @param id The name of the variable to be made global.
     */
    public void defineGlobal(String id) {
        if (!globals.exists(id))
            globals.put(id, new Value());

        getStackFrame().globalInScope(id);
    }

    /**
     * Sets (assigns) the given symbol (variable) to the given value within the current frame.
     *
     * @param symbol The name of the variable to assign
     * @param v      The value to assign it
     */
    public void setVariable(String symbol, Value v) {
        if (globals.exists(symbol) && getStackFrame().isGlobalInScope(symbol))
            globals.put(symbol, v);
        else
            getStackFrame().getSymbols().put(symbol, v);
    }

    /**
     * Puts a value into the given variable, possibly mutating only a portion of the existing value depending on
     * the given chunk and preposition supplied.
     *
     * @param symbol The name of the symbol (variable) to change
     * @param preposition A preposition indicating whether the value will be placed before, after, or into (replacing)
     *                    the existing value
     * @param chunk A chunk of the variable to be mutated, or the entire value if null
     * @param value The value to be put into the mutated portion of the variable.
     * @throws HtException Thrown if an error occurs mutating the variable (i.e., an invalid chunk was specified)
     */
    public void setVariable(String symbol, Preposition preposition, Chunk chunk, Value value) throws HtException {

        // When mutating the value of an un-scoped symbol, do not resolve the value of that symbol to be the symbols's
        // name itself.
        Value mutable = isVariableInScope(symbol) ? getVariable(symbol) : new Value();

        // Operating on a chunk of the existing value
        if (chunk != null)
            mutable = Value.setChunk(this, mutable, preposition, chunk, value);
        else
            mutable = Value.setValue(mutable, preposition, value);

        setVariable(symbol, mutable);
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

        if (globals.exists(symbol) && getStackFrame().isGlobalInScope(symbol))
            value = globals.get(symbol);
        else if (getStackFrame().getSymbols().exists(symbol))
            value = getStackFrame().getSymbols().get(symbol);

            // Allow the user to refer to literals without quotation marks
        else
            value = new Value(symbol);

        return value;
    }

    /**
     * Determines if the given symbol name refers to an in-scope variable (local or global).
     *
     * @param symbol The symbol (variable name) to test
     * @return True if the symbol is an in-scope variable, false otherwise
     */
    private boolean isVariableInScope(String symbol) {
        return globals.exists(symbol) && getStackFrame().isGlobalInScope(symbol) || getStackFrame().getSymbols().exists(symbol);
    }

    /**
     * Determines if the user requested to abort script execution since the time the current script began
     * executing.
     *
     * @return True if script should be aborted, false otherwise
     */
    public boolean didAbort() {
        Long breakTime = KeyboardManager.getInstance().getBreakTime();
        long startTime = getStackFrame().getCreationTimeMs();

        return breakTime != null && breakTime > startTime;
    }

    /**
     * Gets the part model associated with the specified part.
     *
     * @param ps The part specifier
     * @return The part's model
     * @throws PartException Thrown if no such part exists
     */
    public PartModel getPart(PartSpecifier ps) throws PartException {
        return WyldCard.getInstance().getActiveStack().getStackModel().findPart(this, ps);
    }

    /**
     * Gets the value of a property assigned to a given part.
     *
     * @param property The name of the property to retrieve
     * @param ps       A part's specifier, or null to indicate a HyperCard property
     * @return The value of the requested property
     * @throws NoSuchPropertyException Thrown if the property does not exist on the given part
     * @throws PartException           Thrown if the part does not exist
     */
    public Value getProperty(String property, PartSpecifier ps) throws NoSuchPropertyException, PartException {
        if (ps == null) {
            return HyperCardProperties.getInstance().getProperty(this, property);
        } else {
            return getPart(ps).getProperty(this, property);
        }
    }

    /**
     * Sets the value of a property assigned to a given part.
     *
     * @param property    The name of the property to set
     * @param ps          The PartSpecifier identifying the part, or null to specify a HyperCard property
     * @param preposition A preposition indicating where to place the value
     * @param chunk       When non-null, indicates that a chunk of the property should be mutated
     * @param value       The value to place into the property
     * @throws HtException Thrown if an error occurs setting the property
     */
    public void setProperty(String property, PartSpecifier ps, Preposition preposition, Chunk chunk, Value value) throws HtException {
        Value mutable = getProperty(property, ps);

        if (chunk != null) {
            mutable = Value.setChunk(this, mutable, preposition, chunk, value);
        } else {
            mutable = Value.setValue(mutable, preposition, value);
        }

        if (ps == null) {
            HyperCardProperties.getInstance().setProperty(this, property, mutable);
        } else {
            getPart(ps).setProperty(this, property, mutable);
        }
    }

    /**
     * Returns the card in scope of this execution context. That is, the card that the currently executing script should
     * interrogate when looking for parts and properties.
     * <p>
     * In most cases, this method returns the card visible to the user (not accounting for screen lock; equivalent to
     * {@link WyldCard#getActiveStackDisplayedCard()} but during certain operations (like card sorting) this method may return a
     * different value.
     * <p>
     * In general, scripts should always use this method for getting a reference to the active card; UI elements (like
     * menus and palettes) should use {@link WyldCard#getActiveStackDisplayedCard()}.
     *
     * @return The active card in the context of this script execution.
     */
    public CardPart getCurrentCard() {
        CardPart currentCard = this.card;
        if (currentCard == null) {
            return WyldCard.getInstance().getActiveStackDisplayedCard();
        } else {
            return currentCard;
        }
    }

    /**
     * Sets the card context in which the current script is executing. That is, when a script calls for "card field 1"
     * the script is referring to the first card on the CardPart passed to this method.
     * <p>
     * This is typically the same card as the one being displayed, but can vary during sort and find commands.
     *
     * @param card The card representing the context in which the current script is executing.
     */
    public void setCurrentCard(CardPart card) {
        this.card = card;
    }

    /**
     * Gets the stack which this script is operating on.
     *
     * @return The current stack
     */
    public StackPart getCurrentStack() {
        return WyldCard.getInstance().getActiveStack();
    }

    /**
     * Gets the current value of the implicit variable 'it' in this context.
     *
     * @return The value of 'it'.
     */
    public Value getIt() {
        return getVariable("it");
    }

    /**
     * Sets the current value of the implicit 'it' variable in this context.
     *
     * @param value The value of 'it'.
     */
    public void setIt(Object value) {
        setVariable("it", new Value(value));
    }

    /**
     * Gets the message (i.e., the name of the handler or function) that was invoked to cause this script to execute.
     *
     * @return The name of message.
     */
    public String getMessage() {
        return getStackFrame().getMessage();
    }

    /**
     * Sets the message (i.e., the name of the handler or function) that was invoked to cause this script to execute.
     *
     * @param message The message name invoked.
     */
    public void setMessage(String message) {
        this.getStackFrame().setMessage(message);
    }

    /**
     * Gets the arguments passed to this handler or function. Does not return the parameter names, but is named this
     * way to correspond with HyperTalk's 'the params' function (which also ought to be called 'the args', but alas).
     *
     * @return A list of arguments passed to this handler in the order they were passed.
     */
    public List<Value> getParams() {
        return this.getStackFrame().getParams();
    }

    /**
     * Sets the arguments passed to this handler or function.
     * @param params The list of arguments.
     */
    public void setParams(List<Value> params) {
        this.getStackFrame().setParams(params);
    }

    /**
     * Sets the value of me in this context.
     *
     * @param me The part referred to as 'me'
     */
    public void pushMe(PartSpecifier me) {
        meStack.push(me);
    }

    public PartSpecifier popMe() {
        return meStack.pop();
    }

    /**
     * Gets a specifier referencing the part referred to as 'me'.
     *
     * @return The part referred to as me.
     */
    public PartSpecifier getMe() {
        if (meStack.size() == 0) {
            return getTarget();
        } else {
            return meStack.peek();
        }
    }

    /**
     * Gets "the result" (a special property holding the last produced error message; mutated only by certain commands).
     *
     * @return The result; may be the empty string to denote no error.
     */
    public Value getResult() {
        return this.result == null ? new Value() : this.result;
    }

    /**
     * Sets "the result" (a special property holding the last produced error message; mutated only by certain commands).
     *
     * @param result An error message
     */
    public void setResult(Value result) {
        this.result = result;
    }

    /**
     * Gets "the target", that is, the first object in the message passing hierarchy that received the message.
     *
     * @return A PartSpecifier representing the target
     */
    public PartSpecifier getTarget() {
        return theTarget;
    }

    /**
     * Sets "the target", that is, the first object in the message passing hierarchy that received the message.
     *
     * @param theTarget The target PartSpecifier
     */
    public void setTarget(PartSpecifier theTarget) {
        this.theTarget = theTarget;
    }

    /**
     * Sends a message to a part on the current card.
     *
     * @param ps        A specifier identifying the part
     * @param message   The message to be sent
     * @param arguments Message arguments
     * @throws PartException Thrown if the specified part does not exist
     */
    public void sendMessage(PartSpecifier ps, String message, ListExp arguments) throws PartException {
        PartModel thePart = getPart(ps);

        if (thePart != null) {
            thePart.receiveMessage(this, message, arguments);
        } else {
            throw new PartException("No such part.");
        }
    }
}
