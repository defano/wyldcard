/*
 * StatChooseCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.ToolsContext;
import com.defano.hypertalk.ast.common.Tool;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class StatChooseCmd extends Statement {

    private final Expression toolExpression;

    public StatChooseCmd(Expression toolExpression) {
        this.toolExpression = toolExpression;
    }

    public void execute() throws HtException {
        Value toolId = toolExpression.evaluate();
        Tool tool = toolId.isInteger() ? Tool.byNumber(toolId.integerValue()) : Tool.byName(toolId.stringValue());

        ToolsContext.getInstance().setSelectedTool(tool);
    }

}
