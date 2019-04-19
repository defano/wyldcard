package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.awt.mouse.MouseManager;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class ClickVFunc extends Expression {

    @Inject
    private MouseManager mouseManager;

    public ClickVFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) {
        return new Value(mouseManager.getClickLoc().y);
    }
}
