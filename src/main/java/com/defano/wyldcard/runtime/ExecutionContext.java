package com.defano.wyldcard.runtime;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.chunk.Chunk;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtNoSuchPropertyException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.StackManager;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.Part;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.parts.card.CardLayerPart;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.callstack.CallStack;
import com.defano.wyldcard.runtime.callstack.StackFrame;
import com.defano.wyldcard.runtime.symbol.BasicSymbolTable;
import com.defano.wyldcard.runtime.symbol.SymbolTable;

import java.util.List;

/**
 * Represents the state of HyperCard during the execution of a HyperTalk script.
 * <p>
 * This object maintains the call stack (including local and global variables, see {@link StackFrame}); the part
 * referred to as 'me'; the stack referred to as 'this stack' (the one we're operating on); the current card (typically
 * the card presently displayed, but may vary during certain operations like card sorting); the part referred to as
 * 'the target'; the value returned by 'the result'; and any pending visual effect to apply when the screen is next
 * unlocked.
 * <p>
 * Binding: When a script is executing, it must execute in the context of a specific stack and card (i.e., 'card 3'
 * refers to the third card of which stack?; 'cd btn 2' of which card?). This is problematic when multiple stacks are
 * open and active (and each potentially may have their own scripts executing concurrently), or when navigation is
 * underway (as occurs during 'closeCard' and 'openCard' messages, or if the user navigates to a different card while a
 * script is executing).
 * <p>
 * To solve this problem, an execution context is said to either be 'bound' or 'unbound'. An unbound execution context
 * refers to whichever stack is currently in focus in the windowing system. A bound execution context refers to a
 * specific stack irrespective of whether that stack has window focus. UI commands (like menubar commands) should
 * typically use an unbound context (referring to which stack is in focus), but messages sent to a part or stack should
 * utilize a bound context.
 * <p>
 * A note about threading: When HyperCard sends a message to a part (like 'mouseDown') the handler associated with that
 * message executes in its own thread. Any messages sent or functions invoked from that system message handler execute
 * in the same thread. Thus, each system message effectively produces its own thread (albeit threads are pooled and
 * the number of concurrent script threads are limited).
 */
public class ExecutionContext {

    // Throw 'too much recursion' if HyperTalk call stack exceeds this depth
    private final static int MAX_CALL_STACK_DEPTH = 256;

    // Globals are shared across all contexts... that what makes them global :)
    private final static SymbolTable globals = new BasicSymbolTable();

    private StackPart stack;                                // WyldCard stack that this script is bound to
    private CardPart card;                                  // "Current" card in the context of this execution
    private CallStack callStack = new CallStack();          // HyperTalk Call stack
    private Value result;                                   // Value returned by 'the result'
    private PartSpecifier theTarget;                        // Part that the message was initially sent
    private boolean isStaticContext;                        // Is message box evaluation?
    private VisualEffectSpecifier visualEffect;             // Visual effect to use to unlock screen

    /**
     * Creates a execution context bound to the currently-focused stack and card.
     * <p>
     * Typically, the message box and menus should execute in a dynamic context; scripts attached to parts, cards,
     * backgrounds or stacks should use {@link #ExecutionContext(Part)}.
     */
    public ExecutionContext() {
        StackPart boundStack = WyldCard.getInstance().getStackManager().getFocusedStack();

        if (boundStack != null) {
            bindCard(boundStack.getDisplayedCard());
        }
    }

    /**
     * Creates a part-bound execution context. Scripts executing in a part-bound context will operate on whichever stack
     * that the given part is a component of and on whichever card is actively displayed.
     * <p>
     * Scripts attached to parts, cards, backgrounds and stacks should create contexts using this constructor.
     *
     * @param part A part, the owning stack of which will be bound to this execution context. If the given part is not
     *             a component in a stack (either because the type of part is a not a component--like the message box--
     *             or because it hasn't been bound to a stack yet), then this constructor creates a dynamically bound
     *             context. See {@link ExecutionContext()}.
     */
    public ExecutionContext(Part part) {
        if (part instanceof CardLayerPart) {
            bindCard(((CardLayerPart) part).getCard());
        } else if (part instanceof CardPart) {
            bindCard((CardPart) part);
        } else {
            bindStack(part);
        }
    }

    /**
     * Creates a part-bound execution context from a part model. Scripts executing in a part-bound context will operate
     * on whichever stack the given part is a component of.
     * <p>
     * Scripts attached to parts, cards, backgrounds and stacks should create contexts using this constructor or
     * {@link #ExecutionContext(Part)} when a controller object is available.
     *
     * @param partModel The model of the part that is a component of the stack that this context should be bound to.
     * @throws IllegalStateException If the given part model is not bound to a stack.
     */
    public ExecutionContext(PartModel partModel) {
        bindStack(partModel);
    }

    /**
     * Creates an unbound execution context. Scripts executing with a unbound context will operate on whichever stack
     * has focus whenever the 'current' stack is requested. Note that the focused stack may change while the script is
     * executing and this could have unintended consequences.
     *
     * @return An unbound execution context
     */
    public static ExecutionContext unboundInstance() {
        return new ExecutionContext().unbind();
    }

    /**
     * Gets the global variable symbol table.
     * <p>
     * Note that HyperTalk only treats a symbol as a global variable if it explicity declared global with the 'global'
     * keyword. This method returns a table of all global variables, not just those currently in scope. The table
     * returned by this method should typically be filtered with {@link StackFrame#getGlobalsInScope()}.
     *
     * @return A symbol table of all global variables.
     */
    public static SymbolTable getGlobals() {
        return globals;
    }

    /**
     * Unbinds this execution context from the currently bound stack and card.
     * <p>
     * An unbound execution context operates on whichever stack and card currently has window focus; this value may
     * change throughout the execution of a script as the script navigates from card-to-card, or stack-to-stack.
     *
     * @return This execution context
     */
    public ExecutionContext unbind() {
        this.stack = null;
        this.card = null;
        return this;
    }

    /**
     * Binds this execution context to the stack that owns the given part.
     * <p>
     * All future part references encountered in the execution of the script will be interpreted as being a part of the
     * same stack as the given part. This method does not affect card-binding. See {@link #bindCard(CardPart)}.
     *
     * @param part The part whose owning stack this context should be bound to.
     * @return This execution context
     */
    public ExecutionContext bindStack(Part part) {
        if (part != null) {
            return bindStack(part.getOwningStack());
        } else {
            return this;
        }
    }

    /**
     * Binds this execution context to the stack that owns the given part model.
     * <p>
     * All future part references encountered in the execution of the script will be interpreted as being a part of the
     * same stack as the given part.
     *
     * @param partModel The part whose owning stack this context should be bound to.
     * @return This execution context
     */
    public ExecutionContext bindStack(PartModel partModel) {
        StackModel stackModel = partModel.getParentStackModel();

        if (stackModel != null) {
            return bindStack(WyldCard.getInstance().getStackManager().getOpenStack(stackModel));
        } else {
            throw new IllegalStateException("Attempt to bind execution context to a part not attached to a stack.");
        }
    }

    public ExecutionContext bindStack(StackPart stack) {
        if (stack != null) {

            // If stack binding changes in a way that makes card binding invalid, unbind the card.
            if (card != null && !stack.getPartModel().hasCard(card.getPartModel())) {
                this.card = null;
            }

            this.stack = stack;
        }

        return this;
    }

    /**
     * Binds this execution context to the given card (and, implicitly, to the card's owning stack). Has no effect if
     * the given argument is null.
     * <p>
     * This method does not affect stack binding and assumes that the given card is a card in the currently bound stack,
     * but does not validate that this constraint is met.
     *
     * @param cardBinding The card to which this context should be bound
     * @return This execution context
     */
    public ExecutionContext bindCard(CardPart cardBinding) {
        if (cardBinding != null) {
            bindStack(cardBinding.getOwningStack());
            this.card = cardBinding;
        }

        return this;
    }

    /**
     * Pushes a new frame onto the call stack representing a handler or function invocation.
     *
     * @param message   The name of the message (i.e., handler or function) that this frame represents.
     * @param me        The part which the 'me' keyword refers to in this context.
     * @param arguments Evaluated arguments passed to this handler or function.
     */
    public void pushStackFrame(ASTNode callingNode, String message, PartSpecifier me, List<Value> arguments) throws HtException {

        // Kill script execution before we overflow JVM call stack
        if (callStack.size() == MAX_CALL_STACK_DEPTH) {
            throw new HtSemanticException("Too much recursion.");
        }

        getStackFrame().setAstNode(callingNode);
        callStack.push(new StackFrame(me, message, arguments));
    }

    /**
     * Pushes a new frame onto the call stack representing message box text evaluation (i.e., code executing not in the
     * context of a handler or function).
     */
    public void pushStackFrame() throws HtException {

        // Kill script execution before we overflow JVM call stack
        if (callStack.size() == MAX_CALL_STACK_DEPTH) {
            throw new HtSemanticException("Too much recursion.");
        }

        callStack.push(new StackFrame());
    }

    /**
     * Pops the current frame from the call stack.
     */
    public void popStackFrame() {
        callStack.pop();

        if (callStack.size() == 0) {
            expire();
        }
    }

    /**
     * Gets the current stack frame.
     *
     * @return The current stack frame
     */
    public StackFrame getStackFrame() {
        if (callStack.isEmpty()) {
            return new StackFrame();
        }

        return callStack.peek();
    }

    /**
     * Gets the call stack frame at the specified depth where depth=0 returns the current frame, depth=1 returns the
     * frame of the calling handler and so forth. An invalid depth returns null.
     *
     * @param depth The number of call frames down the stack from the current frame.
     * @return The requested frame or null if the requested depth exceeds the depth of the stack.
     */
    public StackFrame peekStackFrame(int depth) {
        if (callStack.size() - depth - 1 < 0) {
            return null;
        } else {
            return callStack.get(callStack.size() - depth - 1);
        }
    }

    /**
     * Gets the number of frames presently pushed onto the call stack.
     *
     * @return The depth of the call stack.
     */
    public int getStackDepth() {
        return callStack.size();
    }

    /**
     * Defines a given symbol as being a global variable in the scope of the current frame.
     *
     * @param id The name of the variable to be made global.
     */
    public void defineGlobal(String id) {
        if (!globals.contains(id))
            globals.set(id, new Value());

        getStackFrame().setGlobalInScope(id);
    }

    /**
     * Sets (assigns) the given symbol (variable) to the given value within the current frame.
     *
     * @param symbol The name of the variable to assign
     * @param v      The value to assign it
     */
    public void setVariable(String symbol, Value v) {
        if (globals.contains(symbol) && getStackFrame().isGlobalInScope(symbol))
            globals.set(symbol, v);
        else
            getStackFrame().getLocalVariables().set(symbol, v);
    }

    /**
     * Puts a value into the given variable, possibly mutating only a portion of the existing value depending on
     * the given chunk and preposition supplied.
     *
     * @param symbol      The name of the symbol (variable) to change
     * @param preposition A preposition indicating whether the value will be placed before, after, or into (replacing)
     *                    the existing value
     * @param chunk       A chunk of the variable to be mutated, or the entire value if null
     * @param value       The value to be put into the mutated portion of the variable.
     * @throws HtException Thrown if an error occurs mutating the variable (i.e., an invalid chunk was specified)
     */
    public void setVariable(String symbol, Preposition preposition, Chunk chunk, Value value) throws HtException {

        // When mutating the value of an un-scoped symbol, do not resolve the value of that symbol to be the symbols's
        // name itself.
        Value mutable = isVariableInScope(symbol) ? getVariable(symbol) : new Value();

        // Operating on a chunk of the existing value
        if (chunk != null)
            mutable = Value.ofMutatedChunk(this, mutable, preposition, chunk, value);
        else
            mutable = Value.ofValue(mutable, preposition, value);

        setVariable(symbol, mutable);
    }

    /**
     * Gets the value assigned to a symbol (variable). If the variable is an in-scope global, returns the globally
     * assigned value; if the variable is an in-scope local variable, returns its value. If the variable does not
     * exist, then returns a Value containing the name of the symbol (allows unrecognized symbols to be treated as
     * unquoted string literals, i.e., 'answer hello').
     *
     * @param symbol The symbol/variable whose value should be retrieved.
     * @return The value of the requested symbol.
     */
    public Value getVariable(String symbol) {
        Value value;

        if (globals.contains(symbol) && getStackFrame().isGlobalInScope(symbol)) {
            value = globals.get(symbol);
        } else if (getStackFrame().getLocalVariables().contains(symbol)) {
            value = getStackFrame().getLocalVariables().get(symbol);
        }

        // Allow the user to refer to literals without quotation marks
        else {
            value = new Value(symbol);
        }

        return value;
    }

    /**
     * Determines if the given symbol name refers to an in-scope variable (either local or global).
     *
     * @param symbol The symbol (variable name) to test
     * @return True if the symbol is an in-scope variable, false otherwise
     */
    private boolean isVariableInScope(String symbol) {
        return globals.contains(symbol) && getStackFrame().isGlobalInScope(symbol) ||
                getStackFrame().getLocalVariables().contains(symbol);
    }

    /**
     * Determines if the user requested to abort script execution since the time the current script began
     * executing.
     *
     * @return True if script should be aborted, false otherwise
     */
    public boolean didAbort() {
        Long breakTime = WyldCard.getInstance().getKeyboardManager().getBreakTime();
        long startTime = getStackFrame().getCreationTimeMs();

        return breakTime != null && breakTime > startTime;
    }

    /**
     * Resets the abort flag such that subsequent calls to {@link #didAbort()} will return false until the next time
     * the user aborts (by pressing command-.).
     */
    public void clearAbort() {
        getStackFrame().resetCreationTimeMs();
    }

    /**
     * Gets the part model associated with the specified part. This is a convenience method; part models and part state
     * are not part of the script execution context.
     *
     * @param ps The part specifier
     * @return The part's model
     * @throws HtNoSuchPartException Thrown if no such part exists
     */
    public PartModel getPart(PartSpecifier ps) throws HtNoSuchPartException {
        return WyldCard.getInstance().findPart(this, ps);
    }

    /**
     * Gets the value of a property assigned to a given part. This is a convenience method; properties are not part of
     * the script execution context.
     *
     * @param property The name of the property to retrieve
     * @param ps       A part's specifier, or null to indicate a HyperCard property
     * @return The value of the requested property
     * @throws HtNoSuchPropertyException Thrown if the property does not exist on the given part
     * @throws HtNoSuchPartException     Thrown if the part does not exist
     */
    public Value getProperty(String property, PartSpecifier ps) throws HtException {
        if (ps == null) {
            return WyldCard.getInstance().getWyldCardPart().tryGet(this, property);
        } else {
            return getPart(ps).tryGet(this, property);
        }
    }

    /**
     * Sets the value of a property assigned to a given part. This is a convenience method; properties are not part of
     * the script execution context.
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
            mutable = Value.ofMutatedChunk(this, mutable, preposition, chunk, value);
        } else {
            mutable = Value.ofValue(mutable, preposition, value);
        }

        if (ps == null) {
            WyldCard.getInstance().getWyldCardPart().trySet(this, property, mutable);
        } else {
            getPart(ps).trySet(this, property, mutable);
        }
    }

    /**
     * Returns the card in scope of this execution context. That is, the card that the currently executing script should
     * interrogate when looking for parts and properties.
     * <p>
     * In most cases, this method returns the card visible to the user (not accounting for screen lock; equivalent to
     * {@link StackManager#getFocusedCard()} but during certain operations (like card sorting) this method may
     * return a different value.
     * <p>
     * In general, scripts should always use this method for getting a reference to the active card; UI elements (like
     * menus and palettes) should use {@link StackManager#getFocusedCard()}.
     *
     * @return The active card in the context of this script execution.
     */
    public CardPart getCurrentCard() {
        CardPart currentCard = this.card;
        if (currentCard == null) {
            return getCurrentStack().getDisplayedCard();
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
     * Gets the stack on which this script is operating. For dynamically-bound contexts, this value returns whichever
     * script has window focus; for part-bound contexts this returns the owning stack of the bound part.
     * <p>
     * See {@link ExecutionContext()} and {@link ExecutionContext(Part)}
     *
     * @return The stack that is in scope for this execution.
     */
    public StackPart getCurrentStack() {
        return this.stack == null ? WyldCard.getInstance().getStackManager().getFocusedStack() : this.stack;
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
     * Gets the flag indicating whether this execution context is a static context, that is, whether this execution
     * represents the message box or the debugger's "Evaluate Expression" feature.
     *
     * @return True if this execution is occurring in the static context.
     */
    public boolean isStaticContext() {
        return isStaticContext;
    }

    /**
     * Sets the flag indicating whether this execution context is a static context, that is, whether this execution
     * represents the message box or the debugger's "Evaluate Expression" feature.
     *
     * @param staticContext True if this execution is occurring in the static context, false otherwise.
     */
    public void setStaticContext(boolean staticContext) {
        isStaticContext = staticContext;
    }

    /**
     * Gets the visual effect to use when next unlocking the screen within the current execution frame.
     *
     * @return The visual effect.
     */
    public VisualEffectSpecifier getVisualEffect() {
        return visualEffect;
    }

    /**
     * Sets the visual effect to use when next unlocking the screen within the current execution frame.
     *
     * @param visualEffect The visual effect specification
     */
    public void setVisualEffect(VisualEffectSpecifier visualEffect) {
        this.visualEffect = visualEffect;
    }

    /**
     * Invoked to indicate that the last message handler has completed executing (i.e., the stack depth has returned to
     * zero), and any behaviors are expected to occur when all handlers are finished should fire.
     */
    private void expire() {
        WyldCard.getInstance().getWyldCardPart().resetProperties(this);
    }

    public String getStackTrace() {
        return callStack.getStackTraceString();
    }

    @Override
    public String toString() {
        if (getStackDepth() == 0) {
            return "Global";
        } else if (getStackFrame().getMe() == null) {
            return getStackFrame().getMessage();
        } else {
            return getStackFrame().getMessage() + " (" + getStackFrame().getMe().getHyperTalkIdentifier(this) + ")";
        }
    }
}
