package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.List;

public class EvaluatedMessage implements Message {

    private final String messageName;
    private final List<Value> arguments;

    public EvaluatedMessage(String messageName, List<Value> arguments) {
        this.messageName = messageName;
        this.arguments = arguments;
    }

    @Override
    public String getMessageName() {
        return messageName;
    }

    @Override
    public List<Value> getArguments(ExecutionContext context) {
        return arguments;
    }

    public List<Value> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
