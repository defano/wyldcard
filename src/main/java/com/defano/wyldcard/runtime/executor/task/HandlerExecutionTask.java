package com.defano.wyldcard.runtime.executor.task;

import com.defano.hypertalk.exception.HtException;

import java.util.concurrent.Callable;

/**
 * Represents the executable "task" of running a HyperTalk message handler.
 */
public interface HandlerExecutionTask extends Callable<Boolean> {

    /**
     * Executes the associated HyperTalk handler.
     *
     * @return True if the message was handled, false if it was not handled or passed (i.e., 'pass mouseUp')
     * @throws HtException Thrown if a HyperTalk error occurs while running the handler.
     */
    Boolean call() throws HtException;
}
