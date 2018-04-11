package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.expressions.containers.ContainerExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PutCmd extends Command {

    public final Expression expression;
    public final Preposition preposition;
    public final Expression container;
    
    public PutCmd(ParserRuleContext context, Expression e, Preposition p, Expression d) {
        super(context, "put");

        expression = e;
        preposition = p;
        container = d;
    }
    
    public void onExecute(ExecutionContext context) throws HtException {
        ContainerExp factor = container.factor(context, ContainerExp.class, new HtSemanticException("Can't put things into that."));
        factor.putValue(context, expression.evaluate(context), preposition);
    }
}
