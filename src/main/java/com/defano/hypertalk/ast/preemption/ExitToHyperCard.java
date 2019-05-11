package com.defano.hypertalk.ast.preemption;

import com.defano.hypertalk.exception.HtException;

/**
 * Represents the 'exit to HyperCard' command; terminates the current handler and all other pending handlers in the
 * current thread (that is, pops the entire call stack and halts execution of the current script thread).
 */
public class ExitToHyperCard extends HtException {
    public ExitToHyperCard() {
        super("Exit to HyperCard");
    }
}
