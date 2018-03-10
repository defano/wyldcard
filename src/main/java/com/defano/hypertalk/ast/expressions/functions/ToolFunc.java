package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import org.antlr.v4.runtime.ParserRuleContext;

public class ToolFunc extends Expression {

    public ToolFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate() {
        return new Value(ToolsContext.getInstance().getSelectedTool().getPrimaryToolName().toLowerCase() + " tool");
    }
}
