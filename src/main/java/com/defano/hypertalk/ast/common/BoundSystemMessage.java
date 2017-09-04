package com.defano.hypertalk.ast.common;

public class BoundSystemMessage {

    public final SystemMessage message;
    public final ExpressionList boundArguments;

    public BoundSystemMessage(SystemMessage message) {
        this.message = message;
        this.boundArguments = new ExpressionList();
    }

    public BoundSystemMessage(SystemMessage message, ExpressionList boundArguments) {
        this.message = message;
        this.boundArguments = boundArguments;
    }
}
