package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class MouseFunc extends Expression {

    @Inject
    private MouseManager mouseManager;

    public MouseFunc(ParserRuleContext context) {
        super(context);
    }
    
    public Value onEvaluate(ExecutionContext context) {
        return mouseManager.isMouseDown() ? new Value("down") : new Value("up");
    }
}
