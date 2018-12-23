package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.DefaultToolsManager;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ChooseCmd extends Command {

    private final Expression toolExpression;

    public ChooseCmd(ParserRuleContext context, Expression toolExpression) {
        super(context, "choose");
        this.toolExpression = toolExpression;
    }

    public void onExecute(ExecutionContext context) throws HtException {
        WyldCard.getInstance().getToolsManager().forceToolSelection(getChosenTool(context), false);
    }

    protected ListExp getEvaluatedMessageArguments(ExecutionContext context) throws HtException {
        ToolType theTool = getChosenTool(context);
        return ListExp.fromValues(null, new Value(theTool.getPrimaryToolName()), new Value(theTool.getToolNumber()));
    }

    private ToolType getChosenTool(ExecutionContext context) throws HtException {
        Value toolId = toolExpression.evaluate(context);
        return toolId.isInteger() ? ToolType.byNumber(toolId.integerValue()) : ToolType.byName(toolId.stringValue());
    }
}
