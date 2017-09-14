package com.defano.hypertalk.ast.common;

import com.google.common.collect.Lists;

public class BoundSystemMessage {

    public final SystemMessage message;
    public final ExpressionList boundArguments;

    public BoundSystemMessage(SystemMessage message) {
        this.message = message;
        this.boundArguments = new ExpressionList();
    }

    public BoundSystemMessage(SystemMessage message, Value... boundArguments) {
        this.message = message;
        this.boundArguments = new ExpressionList(Lists.newArrayList(boundArguments));
    }
}
