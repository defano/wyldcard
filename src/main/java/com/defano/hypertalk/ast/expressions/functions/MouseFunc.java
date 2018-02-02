package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypercard.awt.MouseManager;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import org.antlr.v4.runtime.ParserRuleContext;

public class MouseFunc extends Expression {

    public MouseFunc(ParserRuleContext context) {
        super(context);
    }
    
    public Value onEvaluate() {
        return MouseManager.getInstance().isMouseDown() ? new Value("down") : new Value("up");
    }
}
