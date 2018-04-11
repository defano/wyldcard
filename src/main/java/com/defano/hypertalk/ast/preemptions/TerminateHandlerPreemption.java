package com.defano.hypertalk.ast.preemptions;

/**
 * Represents an interruption to the flow of control in the current handler of function; causes control to exit the
 * current handler or function.
 */
public class TerminateHandlerPreemption extends Preemption {

    /**
     * The name of the handler or function requested to break from
     */
    private final String handlerName;

    public TerminateHandlerPreemption(String handlerName) {
        this.handlerName = handlerName;
    }

    /**
     * Gets the name of handler identified in the return, pass or exit statement which generated this breakpoint.
     * Statements which do no identify a block name (like 'return') produce null.
     *
     * @return The identified block name, or null of breakpoint identifies no name.
     */
    public String getHandlerName() {
        return handlerName;
    }
}
