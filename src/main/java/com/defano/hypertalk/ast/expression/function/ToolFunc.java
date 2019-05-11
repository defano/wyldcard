package com.defano.hypertalk.ast.expression.function;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expression.Expression;
import org.antlr.v4.runtime.ParserRuleContext;

public class ToolFunc extends Expression {

    public ToolFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) {
        return new Value(WyldCard.getInstance().getPaintManager().getSelectedTool().getPrimaryToolName().toLowerCase() + " tool");
    }
}
