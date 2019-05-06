package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {

    private String messageName;
    private List<Value> arguments;
    private ListExp argumentsExpr;

    private MessageBuilder(String messageName) {
        this.messageName = messageName;
        this.arguments = new ArrayList<>();
    }

    public static MessageBuilder named(Object name) {
        return new MessageBuilder(name.toString());
    }

    /**
     * Creates a message by parsing the given string into a message name a list of zero or more argument expressions.
     * <p>
     * For example, if the text contains 'doSomething (cd fld 1), x` the value of 'cd fld 1' and 'x' will be evaluated
     * when {@link Message#getArguments(ExecutionContext)} is called using the provided execution context.
     * <p>
     * NOTE that the message returned has the limitation that its arguments may only be retrieved on a script execution
     * thread because of the need to invoke the runtime environment in order to evaluate argument expressions.
     *
     * @param text The text of the message to be sent.
     * @return A message object, providing a late-binding (evaluation) of message arguments.
     */
    public static Message fromString(String text) {
        return new LiteralMessage(text);
    }

    public static Message emptyMessage() {
        return new EvaluatedMessage("", new ArrayList<>());
    }

    public MessageBuilder withArgument(Object argument) {
        arguments.add(new Value(argument));
        return this;
    }

    public MessageBuilder withArguments(List<Value> args) {
        this.arguments.addAll(args);
        return this;
    }

    public MessageBuilder withArguments(ListExp argumentsExpr) {
        this.argumentsExpr = argumentsExpr;
        return this;
    }

    public MessageBuilder withArgumentExpression(ExecutionContext context, Expression expression) throws HtException {
        this.arguments.addAll(expression.evaluateAsList(context));
        return this;
    }

    public Message build() {
        if (argumentsExpr != null) {
            return new ExpressionMessage(messageName, argumentsExpr);
        } else {
            return new EvaluatedMessage(messageName, arguments);
        }
    }
}
