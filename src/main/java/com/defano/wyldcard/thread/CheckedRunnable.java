package com.defano.wyldcard.thread;

public interface CheckedRunnable<E extends Exception> {
    void run() throws E;
}
