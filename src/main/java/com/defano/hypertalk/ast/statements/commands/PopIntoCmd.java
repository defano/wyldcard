package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.containers.ContainerExp;
import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.NavigationManager;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class PopIntoCmd extends Command {

    private final Preposition preposition;
    private final Expression expression;

    @Inject
    private NavigationManager navigationManager;

    public PopIntoCmd(ParserRuleContext context, Preposition preposition, Expression expression) {
        super(context, "pop");
        this.preposition = preposition;
        this.expression = expression;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        Destination popped = navigationManager.pop();

        if (popped != null) {
            ContainerExp container = expression.factor(context, ContainerExp.class, new HtSemanticException("Cannot pop into that."));
            container.putValue(context, new Value(popped.getHypertalkIdentifier(context)), preposition);
        }
    }
}
