package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.expression.ListExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.util.List;

/**
 * A HyperTalk message represented by a list of HyperTalk expressions.
 */
public class ExpressionMessage implements Message {

    private final String messageName;
    private final ListExp messageArgumentsExpr;

    public ExpressionMessage(String messageName, ListExp messageArgumentsExpr) {
        this.messageName = messageName;
        this.messageArgumentsExpr = messageArgumentsExpr;
    }

    @Override
    public String getMessageName() {
        return messageName;
    }

    @Override
    public List<Value> evaluateArguments(ExecutionContext context) throws HtException {
        return messageArgumentsExpr.evaluateAsList(context);
    }
}
