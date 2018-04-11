package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.expressions.containers.ContainerExp;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class MultiplyCmd extends Command {

    private final Expression expression;
    private final Expression container;

    public MultiplyCmd(ParserRuleContext context, Expression source, Expression container) {
        super(context, "multiply");

        this.expression = source;
        this.container = container;
    }

    public void onExecute(ExecutionContext context) throws HtException {
        ContainerExp factor = container.factor(context, ContainerExp.class, new HtSemanticException("Cannot multiply that."));
        factor.putValue(context, factor.evaluate(context).multiply(expression.evaluate(context)), Preposition.INTO);
    }
}
