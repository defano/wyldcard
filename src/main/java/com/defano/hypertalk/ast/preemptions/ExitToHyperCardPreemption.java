package com.defano.hypertalk.ast.preemptions;

/**
 * Represents the 'exit to HyperCard' command; terminate the current handler and all other pending handlers in the
 * current thread (that is, pops the entire call stack and exits execution).
 */
public class ExitToHyperCardPreemption extends RuntimeException {
}
