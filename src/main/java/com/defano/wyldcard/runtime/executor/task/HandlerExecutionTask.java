package com.defano.wyldcard.runtime.executor.task;

import com.defano.hypertalk.exception.HtException;

import java.util.concurrent.Callable;

public interface HandlerExecutionTask extends Callable<Boolean> {
    Boolean call() throws HtException;
}
