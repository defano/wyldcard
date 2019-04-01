package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.compiler.Compiler;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {

    private String messageName;
    private List<Value> arguments;

    private MessageBuilder(String messageName) {
        this.messageName = messageName;
        this.arguments = new ArrayList<>();
    }

    public static MessageBuilder named(Object name) {
        return new MessageBuilder(name.toString());
    }

    public static Message fromString(ExecutionContext context, String text) {
        String message = text.trim().split("\\s")[0];
        ArrayList<Value> arguments = new ArrayList<>();

        for (String component : text.trim().substring(message.length()).split(",")) {
            arguments.add(Compiler.blockingEvaluate(component.trim(), context));
        }

        return MessageBuilder
                .named(message)
                .withArguments(arguments)
                .build();
    }

    public MessageBuilder withArgument(Object argument) {
        arguments.add(new Value(argument));
        return this;
    }

    public MessageBuilder withArguments(List<Value> args) {
        this.arguments.addAll(args);
        return this;
    }

    public MessageBuilder withArgumentExpression(ExecutionContext context, Expression expression) throws HtException {
        this.arguments.addAll(expression.evaluateAsList(context));
        return this;
    }

    public Message build() {
        return new Message() {
            @Override
            public String getMessageName() {
                return messageName;
            }

            @Override
            public List<Value> getArguments() {
                return arguments;
            }
        };
    }
}
