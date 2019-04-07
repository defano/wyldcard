package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;

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
    public List<Value> getArguments(ExecutionContext context) throws HtException {
        return messageArgumentsExpr.evaluateAsList(context);
    }
}
