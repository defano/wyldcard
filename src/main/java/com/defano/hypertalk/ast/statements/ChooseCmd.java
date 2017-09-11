/*
 * StatChooseCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.ToolsContext;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Tool;
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
        ToolsContext.getInstance().setSelectedTool(getChosenTool(), false);
    }

    protected ExpressionList getEvaluatedMessageArguments() throws HtSemanticException {
        Tool theTool = getChosenTool();
        ExpressionList arguments = new ExpressionList();

        arguments.addArgument(new LiteralExp(theTool.toolNames.get(0)));
        arguments.addArgument(new LiteralExp(theTool.toolNumber));

        return arguments;
    }

    private Tool getChosenTool() throws HtSemanticException {
        Value toolId = toolExpression.evaluate();
        return toolId.isInteger() ? Tool.byNumber(toolId.integerValue()) : Tool.byName(toolId.stringValue());
    }
}
