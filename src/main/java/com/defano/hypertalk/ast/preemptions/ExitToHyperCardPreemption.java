package com.defano.hypertalk.ast.preemptions;

/**
 * Represents the 'exit to HyperCard' command; terminates the current handler and all other pending handlers in the
 * current thread (that is, pops the entire call stack and halts execution of the current script thread).
 */
public class ExitToHyperCardPreemption extends RuntimeException {
}
