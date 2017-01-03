package hypertalk.ast.statements;

import hypercard.context.ToolsContext;
import hypertalk.ast.common.Tool;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtException;

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
