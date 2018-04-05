package com.defano.wyldcard.debug.watch.message;

import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;

public class HandlerInvocation implements Comparable<HandlerInvocation> {

    private final String thread;
    private final String message;
    private final PartSpecifier recipient;
    private final int stackDepth;
    private final boolean messageHandled;

    private final long sequence = System.currentTimeMillis();

    public HandlerInvocation(String thread, String message, PartSpecifier recipient, int stackDepth, boolean msgHandled) {
        this.thread = thread;
        this.message = message;
        this.recipient = recipient;
        this.stackDepth = stackDepth;
        this.messageHandled = msgHandled;
    }

    public String getThread() {
        return thread;
    }

    public String getMessage() {
        return message;
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
