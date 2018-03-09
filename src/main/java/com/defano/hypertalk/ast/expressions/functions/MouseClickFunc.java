package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypercard.awt.MouseManager;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class MouseClickFunc extends Expression {

    public MouseClickFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate() throws HtException {
        Long lastClickTime = MouseManager.getInstance().getClickTimeMs();
        boolean mouseClicked = lastClickTime != null && (lastClickTime > ExecutionContext.getContext().getFrame().getCreationTimeMs());
        return new Value(mouseClicked);
    }
}
