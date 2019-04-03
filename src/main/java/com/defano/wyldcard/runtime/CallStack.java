package com.defano.wyldcard.runtime;

import java.util.Stack;

public class CallStack extends Stack<StackFrame> {

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (StackFrame stackFrame : this) {
            builder.append(stackFrame.toString()).append("\n");
        }

        return builder.toString();
    }
}
