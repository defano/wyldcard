package com.defano.wyldcard.debug.message;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.message.MessageBuilder;
import com.defano.wyldcard.message.SystemMessage;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class HandlerInvocation implements Comparable<HandlerInvocation>, Message {

    private final String thread;
    private final Message message;
    private final PartSpecifier recipient;
    private final int stackDepth;
    private final boolean messageHandled;
    private final long sequence;
    private final boolean isTarget;
    private final boolean isPeriodicMessage;

    private static AtomicLong globalSequence = new AtomicLong(0);

    public HandlerInvocation(String thread, String messageName, List<Value> messageArguments, PartSpecifier recipient, boolean isTarget, int stackDepth, boolean msgHandled) {
        this(thread, MessageBuilder.named(messageName).withArguments(messageArguments).build(), recipient, isTarget, stackDepth, msgHandled);
    }

    private HandlerInvocation(String thread, Message message, PartSpecifier recipient, boolean isTarget, int stackDepth, boolean msgHandled) {
        this.thread = thread;
        this.message = message;
        this.recipient = recipient;
        this.stackDepth = stackDepth;
        this.messageHandled = msgHandled;
        this.isTarget = isTarget;
        this.sequence = globalSequence.incrementAndGet();

        SystemMessage systemMessage = SystemMessage.fromMessageName(message.getMessageName());
        this.isPeriodicMessage = systemMessage != null && systemMessage.isPeriodicMessage();
    }

    public String getThread() {
        return thread;
    }

    @Override
    public String getMessageName() {
        return message.getMessageName();
    }

    @Override
    public List<Value> evaluateArguments(ExecutionContext context) throws HtException {
        return message.evaluateArguments(context);
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

    public boolean isPeriodicMessage() {
        return isPeriodicMessage;
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
