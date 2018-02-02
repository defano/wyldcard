package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypercard.awt.MouseManager;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import org.antlr.v4.runtime.ParserRuleContext;

public class MouseLocFunc extends Expression {

    public MouseLocFunc(ParserRuleContext context) {
        super(context);
    }
    
    public Value onEvaluate() {
        return new Value(MouseManager.getInstance().getMouseLoc());
    }
}
