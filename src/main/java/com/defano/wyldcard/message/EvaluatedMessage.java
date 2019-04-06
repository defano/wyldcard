package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;

public class EvaluatedMessage implements Message {

    private final String messageName;
    private final List<Value> arguments;

    public EvaluatedMessage(String messageName, List<Value> arguments) {
        this.messageName = messageName;
        this.arguments = arguments;
    }

    @Override
    public String getMessageName(ExecutionContext context) {
        return messageName;
    }

    @Override
    public List<Value> getArguments(ExecutionContext context) {
        return arguments;
    }
}