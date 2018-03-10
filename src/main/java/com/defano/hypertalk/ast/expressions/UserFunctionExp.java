package com.defano.hypertalk.ast.expressions;

import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UserFunctionExp extends Expression {

    public final String function;
    public final ListExp arguments;

    public UserFunctionExp(ParserRuleContext context, String function, ListExp arguments) {
        super(context);
        this.function = function;
        this.arguments = arguments;
    }

    public Value onEvaluate() throws HtException {

        if (!ExecutionContext.getContext().hasMe()) {
            throw new HtSemanticException("Cannot invoke user-defined functions here.");
        }

        PartSpecifier ps = ExecutionContext.getContext().getMe();
        PartModel part = ExecutionContext.getContext().getPart(ps);

        return part.invokeFunction(function, arguments);
    }
}
