package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class MouseClickFunc extends Expression {

    public MouseClickFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        Long lastClickTime = MouseManager.getInstance().getClickTimeMs();
        boolean mouseClicked = lastClickTime != null && (lastClickTime > context.getStackFrame().getCreationTimeMs());
        return new Value(mouseClicked);
    }
}
