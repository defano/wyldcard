package com.defano.hypertalk.ast.expressions;

import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UserFunctionExp extends Expression {

    public final String function;
    public final ListExp arguments;

    public UserFunctionExp(ParserRuleContext context, String function, ListExp arguments) {
        super(context);
        this.function = function;
        this.arguments = arguments;
    }

    public Value onEvaluate(ExecutionContext context) throws HtException {

        PartSpecifier ps = context.getStackFrame().getMe();
        PartModel part = context.getPart(ps);

        return part.invokeFunction(context, function, arguments);
    }
}
