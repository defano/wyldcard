package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.containers.ContainerExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
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
    
    public void onExecute () throws HtException {
        ContainerExp factor = container.factor(ContainerExp.class, new HtSemanticException("Can't put things into that."));
        factor.putValue(expression.evaluate(), preposition);
    }
}
