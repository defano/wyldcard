package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.containers.ContainerExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SubtractCmd extends Command {

    private final Expression expression;
    private final Expression container;

    public SubtractCmd(ParserRuleContext context, Expression source, Expression container) {
        super(context, "subtract");

        this.expression = source;
        this.container = container;
    }

    public void onExecute() throws HtException {
        ContainerExp factor = container.factor(ContainerExp.class, new HtSemanticException("Can't subtract from that."));
        factor.putValue(factor.evaluate().subtract(expression.evaluate()), Preposition.INTO);
    }
}
