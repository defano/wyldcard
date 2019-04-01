package com.defano.hypertalk.ast.model;

import com.defano.wyldcard.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoundSystemMessage implements Message {

    public final SystemMessage message;
    private List<Value> boundArguments = new ArrayList<>();

    public BoundSystemMessage(SystemMessage message) {
        this.message = message;
    }

    public BoundSystemMessage(SystemMessage message, Value... boundArguments) {
        this.message = message;
        this.boundArguments = Arrays.asList(boundArguments);
    }

    @Override
    public String getMessageName() {
        return message.messageName;
    }

    @Override
    public List<Value> getArguments() {
        return boundArguments;
    }
}
