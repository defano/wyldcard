/*
 * StatChooseCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.ToolType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class ChooseCmd extends Command {

    private final Expression toolExpression;

    public ChooseCmd(Expression toolExpression) {
        super("choose");
        this.toolExpression = toolExpression;
    }

    public void onExecute() throws HtException {
        ToolsContext.getInstance().forceToolSelection(getChosenTool(), false);
    }

    protected ExpressionList getEvaluatedMessageArguments() throws HtSemanticException {
        ToolType theTool = getChosenTool();
        ExpressionList arguments = new ExpressionList();

        arguments.addArgument(new LiteralExp(theTool.getPrimaryToolName()));
        arguments.addArgument(new LiteralExp(theTool.getToolNumber()));

        return arguments;
    }

    private ToolType getChosenTool() throws HtSemanticException {
        Value toolId = toolExpression.evaluate();
        return toolId.isInteger() ? ToolType.byNumber(toolId.integerValue()) : ToolType.byName(toolId.stringValue());
    }
}
