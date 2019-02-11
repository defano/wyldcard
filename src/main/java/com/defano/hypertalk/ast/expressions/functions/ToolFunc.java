package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import org.antlr.v4.runtime.ParserRuleContext;

public class ToolFunc extends Expression {

    public ToolFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) {
        return new Value(WyldCard.getInstance().getToolsManager().getSelectedTool().getPrimaryToolName().toLowerCase() + " tool");
    }
}
