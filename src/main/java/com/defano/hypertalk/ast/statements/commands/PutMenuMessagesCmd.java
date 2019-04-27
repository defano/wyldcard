package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.expressions.containers.MenuExp;
import com.defano.hypertalk.ast.expressions.containers.MenuItemExp;
import com.defano.hypertalk.ast.expressions.factor.FactorAssociation;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class PutMenuMessagesCmd extends Command {

    private final ListExp valueExpr;
    private final Preposition preposition;
    private final Expression menuExpr;
    private final ListExp menuMessagesExpr;

    public PutMenuMessagesCmd(ParserRuleContext context, ListExp valueExpr, Preposition preposition, Expression menuExpr, ListExp messagesExpr) {
        super(context, "put");

        this.valueExpr = valueExpr;
        this.preposition = preposition;
        this.menuExpr = menuExpr;
        this.menuMessagesExpr = messagesExpr;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        List<Value> menuMessages = menuMessagesExpr.divingSingletonEvaluation(context);
        boolean success = menuExpr.factor(context,
                new FactorAssociation<>(MenuItemExp.class, o -> o.putValue(context, valueExpr.evaluate(context), preposition, menuMessages)),
                new FactorAssociation<>(MenuExp.class, o -> o.putValue(context, valueExpr.evaluate(context), preposition, menuMessages))
        );

        if (!success) {
            throw new HtSemanticException("Cannot put a value with menu messages into that.");
        }
    }
}
