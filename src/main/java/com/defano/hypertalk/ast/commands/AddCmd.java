package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.containers.ContainerExp;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class AddCmd extends Command {

    private final Expression expression;
    private final Expression container;

    public AddCmd(ParserRuleContext context, Expression source, Expression container) {
        super(context, "add");

        this.expression = source;
        this.container = container;
    }

    public void onExecute() throws HtException {
        ContainerExp factor = container.factor(ContainerExp.class, new HtSemanticException("Can't add to that."));
        factor.putValue(factor.evaluate().add(expression.evaluate()), Preposition.INTO);
    }
}
