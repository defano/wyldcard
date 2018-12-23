package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class MouseClickFunc extends Expression {

    @Inject
    private MouseManager mouseManager;

    public MouseClickFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        Long lastClickTime = mouseManager.getClickTimeMs();
        boolean mouseClicked = lastClickTime != null && (lastClickTime > context.getStackFrame().getCreationTimeMs());
        return new Value(mouseClicked);
    }
}
