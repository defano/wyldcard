package com.defano.wyldcard.runtime;

import java.util.Stack;

public class CallStack extends Stack<StackFrame> {

    public String getStackTraceString() {
        StringBuilder builder = new StringBuilder();

        for (int idx = this.size() - 1; idx >= 0; idx--) {
            builder.append("\t").append("at ").append(this.get(idx).getStackTraceEntryString()).append("\n");
        }

        return builder.toString();
    }
}
