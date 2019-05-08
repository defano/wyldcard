package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;
import java.util.List;

public class ChooseCmd extends Command {

    private final Expression toolExpression;

    public ChooseCmd(ParserRuleContext context, Expression toolExpression) {
        super(context, "choose");
        this.toolExpression = toolExpression;
    }

    public void onExecute(ExecutionContext context) throws HtException {
        WyldCard.getInstance().getPaintManager().forceToolSelection(getChosenTool(context), false);
    }

    @Override
    protected List<Value> getEvaluatedMessageArguments(ExecutionContext context) throws HtException {
        ToolType theTool = getChosenTool(context);
        return Arrays.asList(new Value(theTool.getPrimaryToolName()), new Value(theTool.getToolNumber()));
    }

    private ToolType getChosenTool(ExecutionContext context) throws HtException {
        Value toolId = toolExpression.evaluate(context);
        return toolId.isInteger() ? ToolType.byNumber(toolId.integerValue()) : ToolType.byName(toolId.toString());
    }
}
