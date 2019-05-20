package com.defano.wyldcard.runtime.callstack;

import java.util.ArrayDeque;

public class CallStack extends ArrayDeque<StackFrame> {

    public String getStackTraceString() {
        StringBuilder builder = new StringBuilder();

        StackFrame[] frames = toArray(new StackFrame[0]);

        for (int idx = frames.length - 1; idx >= 0; idx--) {
            builder.append("\t").append("at ").append(frames[idx].getStackTraceEntryString()).append("\n");
        }

        return builder.toString();
    }
}
