package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.container.ContainerExp;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class SubtractCmd extends Command {

    private final Expression expression;
    private final Expression container;

    public SubtractCmd(ParserRuleContext context, Expression source, Expression container) {
        super(context, "subtract");

        this.expression = source;
        this.container = container;
    }

    public void onExecute(ExecutionContext context) throws HtException {
        ContainerExp factor = container.factor(context, ContainerExp.class, new HtSemanticException("Can't subtract from that."));
        factor.putValue(context, factor.evaluate(context).subtract(expression.evaluate(context)), Preposition.INTO);
    }
}
