package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.awt.mouse.MouseManager;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class MouseClickFunc extends Expression {

    @Inject
    private MouseManager mouseManager;

    public MouseClickFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) {
        Long lastClickTime = mouseManager.getClickTimeMs();
        boolean mouseClicked = lastClickTime != null && (lastClickTime > context.getStackFrame().getCreationTimeMs());
        return new Value(mouseClicked);
    }
}
