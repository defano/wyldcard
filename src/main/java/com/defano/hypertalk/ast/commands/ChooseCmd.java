package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.ToolType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ChooseCmd extends Command {

    private final Expression toolExpression;

    public ChooseCmd(ParserRuleContext context, Expression toolExpression) {
        super(context, "choose");
        this.toolExpression = toolExpression;
    }

    public void onExecute() throws HtException {
        ToolsContext.getInstance().forceToolSelection(getChosenTool(), false);
    }

    protected ExpressionList getEvaluatedMessageArguments() throws HtException {
        ToolType theTool = getChosenTool();
        ExpressionList arguments = new ExpressionList();

        arguments.addArgument(new LiteralExp(null, theTool.getPrimaryToolName()));
        arguments.addArgument(new LiteralExp(null, theTool.getToolNumber()));

        return arguments;
    }

    private ToolType getChosenTool() throws HtException {
        Value toolId = toolExpression.evaluate();
        return toolId.isInteger() ? ToolType.byNumber(toolId.integerValue()) : ToolType.byName(toolId.stringValue());
    }
}
