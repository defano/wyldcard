package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.containers.ContainerExp;
import org.antlr.v4.runtime.ParserRuleContext;

public class MultiplyCmd extends Command {

    private final Expression expression;
    private final ContainerExp container;

    public MultiplyCmd(ParserRuleContext context, Expression source, ContainerExp container) {
        super(context, "multiply");

        this.expression = source;
        this.container = container;
    }

    public void onExecute() throws HtException {
        container.putValue(container.evaluate().multiply(expression.evaluate()), Preposition.INTO);
    }
}
