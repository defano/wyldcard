package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.expressions.ListExp;

public class BoundSystemMessage {

    public final SystemMessage message;
    public final ListExp boundArguments;

    public BoundSystemMessage(SystemMessage message) {
        this.message = message;
        this.boundArguments = new ListExp(null);
    }

    public BoundSystemMessage(SystemMessage message, Value... boundArguments) {
        this.message = message;
        this.boundArguments = ListExp.fromValues(null, boundArguments);
    }
}
