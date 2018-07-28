package com.defano.wyldcard.debug.message;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class HandlerInvocation implements Comparable<HandlerInvocation> {

    private final String thread;
    private final String message;
    private final List<Value> arguments;
    private final PartSpecifier recipient;
    private final int stackDepth;
    private final boolean messageHandled;
    private final long sequence;
    private final boolean isTarget;

    private static AtomicLong globalSequence = new AtomicLong(0);

    public HandlerInvocation(String thread, String message, List<Value> arguments, PartSpecifier recipient, boolean isTarget, int stackDepth, boolean msgHandled) {
        this.thread = thread;
        this.message = message;
        this.arguments = arguments;
        this.recipient = recipient;
        this.stackDepth = stackDepth;
        this.messageHandled = msgHandled;
        this.isTarget = isTarget;

        sequence = globalSequence.incrementAndGet();
    }

    public String getThread() {
        return thread;
    }

    public String getMessage() {
        return message;
    }

    public List<Value> getArguments() {
        return arguments;
    }

    public PartSpecifier getRecipient() {
        return recipient;
    }

    public int getStackDepth() {
        return stackDepth;
    }

    public boolean isMessageHandled() {
        return messageHandled;
    }

    public boolean isTarget() {
        return isTarget;
    }

    @Override
    public int compareTo(HandlerInvocation o) {
        if (this.sequence < o.sequence) {
            return -1;
        } else if (this.sequence == o.sequence) {
            return 0;
        } else {
            return 1;
        }
    }
}
