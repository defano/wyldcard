package com.defano.hypertalk.ast.preemptions;

/**
 * Represents an interruption in the flow of a handler as a result of reaching a 'pass {handlerName}` command.
 */
public class PassPreemption extends TerminateHandlerPreemption {

    /**
     * Constructs a new PassPreemption
     *
     * @param passedMessage The name of the message being passed. In order to be semantically valid, this value must
     *                      match the name of the handler in which its used.
     */
    public PassPreemption(String passedMessage) {
        super(passedMessage);
    }
}
