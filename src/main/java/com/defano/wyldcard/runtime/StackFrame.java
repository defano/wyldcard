package com.defano.wyldcard.runtime;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.symbol.BasicSymbolTable;
import com.defano.wyldcard.runtime.symbol.CompositeSymbolTable;
import com.defano.wyldcard.runtime.symbol.FilteredSymbolTable;
import com.defano.wyldcard.runtime.symbol.SymbolTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StackFrame {

    private final SymbolTable localVariables = new BasicSymbolTable();  // Local variables
    private final List<String> globalsInScope = new ArrayList<>();      // Global variables that are in scope in this frame

    private long creationTime = System.currentTimeMillis();             // Time when this frame was created
    private List<Value> params = new ArrayList<>();                     // Arguments passed to this function/handler
    private String message = "";                                        // The name of this function/handler
    private Value returnValue = new Value();                            // Value returned from this function
    private PartSpecifier me;                                           // The part that 'me' refers to

    /**
     * Create a stack frame representing the invocation of unbound script text (i.e., text entered into the message
     * window).
     */
    public StackFrame() {
        this.me = new PartMessageSpecifier();
    }

    /**
     * Create a stack frame representing the invocation of a handler or user-defined function.
     *
     * @param me        The part to which the 'me' keyword is bound in this context (i.e., the part owning this script)
     * @param message   The message being handled (i.e., the name of the handler or function)
     * @param arguments A list of evaluated arguments to be bound the handler's parameter list. May not be null; provide
     *                  an empty list for invocations not passing arguments.
     */
    public StackFrame(PartSpecifier me, String message, List<Value> arguments) {
        this.message = message;
        this.me = me;
        this.params = arguments;
    }

    /**
     * Gets the time when this stack frame was created (used for determining if certain events occurred while this
     * handler was executing).
     *
     * @return The value of {@link System#currentTimeMillis()} at the moment this frame was created.
     */
    public long getCreationTimeMs() {
        return creationTime;
    }

    /**
     * Sets the creation time of this stack frame to the current time (typically only useful to resetting an abort
     * condition).
     */
    public void resetCreationTimeMs() {
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * Gets the local variables that are in scope for this call stack frame.
     *
     * @return In-scope local variables
     */
    public SymbolTable getLocalVariables() {
        return localVariables;
    }

    /**
     * Gets the global variables that are in-scope in this stack frame (global variables are placed into scope via the
     * 'global' keyword).
     *
     * @return In-scope global variables
     */
    public SymbolTable getScopedGlobalVariables() {
        return new FilteredSymbolTable(ExecutionContext.getGlobals(), globalsInScope);
    }

    /**
     * Gets a symbol table containing all in-scope variables (local and global).
     *
     * @return A read-only table of all visible variables.
     */
    public SymbolTable getVariables() {
        return new CompositeSymbolTable(getScopedGlobalVariables(), getLocalVariables());
    }

    /**
     * Gets the message (i.e., the handler or function) being processed by this frame.
     *
     * @return The name of the message being handled by this frame.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message (i.e., the name of the handler or function) that was invoked to cause this script to execute.
     *
     * @param message The message name invoked.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the arguments passed to this handler or function. Does not return the parameter names, but is named this
     * way to correspond with HyperTalk's 'the params' function (which also ought to be called 'the args', but alas...).
     *
     * @return A list of arguments passed to this handler in the order they were passed.
     */
    public List<Value> getParams() {
        return this.params;
    }

    /**
     * Sets the arguments passed to this handler or function.
     *
     * @param params The list of arguments.
     */
    public void setParams(List<Value> params) {
        this.params = params;
    }

    /**
     * Specifies that the given symbol refers to a global variable that is in-scope in this frame.
     *
     * @param symbol The symbol to designate as being in scope.
     */
    public void setGlobalInScope(String symbol) {
        globalsInScope.add(symbol);
    }

    /**
     * Returns a collection of case-insensitive symbols that are in-scope global variables in this frame.
     *
     * @return The set of in-scope global variables.
     */
    public Collection<String> getGlobalsInScope() {
        return globalsInScope;
    }

    /**
     * Determines if the specified symbol refers to an in-scope global variable.
     *
     * @param symbol The case-insensitive name of the symbol
     * @return True if the symbol is an in-scope global variable, false otherwise.
     */
    public boolean isGlobalInScope(String symbol) {
        return globalsInScope.contains(symbol);
    }

    /**
     * Gets the value returned from this frame. That is, the value specified in the script's 'return' statement.
     *
     * @return The returned value.
     */
    public Value getReturnValue() {
        return returnValue;
    }

    /**
     * Specifies the value returned from this frame / function / handler. Typically set in response to a 'return xxx'
     * statement in script.
     *
     * @param returnValue The returned value
     */
    public void setReturnValue(Value returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * Gets a specifier referencing the part referred to as 'me'.
     *
     * @return The part referred to as me.
     */
    public PartSpecifier getMe() {
        return me;
    }

    /**
     * Sets the part referred to as 'me' in this context.
     *
     * @param me The part referred to as 'me'.
     */
    public void setMe(PartSpecifier me) {
        this.me = me;
    }
}
