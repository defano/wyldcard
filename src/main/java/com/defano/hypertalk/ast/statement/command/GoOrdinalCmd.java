package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.model.enums.Ordinal;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.NavigationManager;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Random;

public class GoOrdinalCmd extends Command {

    @Inject
    private NavigationManager navigationManager;

    private final Ordinal ordinal;

    public GoOrdinalCmd(ParserRuleContext context, Ordinal ordinal) {
        super(context, "go");
        this.ordinal = ordinal;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        int cardCount = context.getCurrentStack().getCardCountProvider().blockingFirst();

        switch (ordinal) {
            case LAST:
                navigationManager.goLastCard(context, context.getCurrentStack());
                break;
            case MIDDLE:
                navigationManager.goCard(context, context.getCurrentStack(), (cardCount - 1) / 2, true);
                break;
            case ANY:
                navigationManager.goCard(context, context.getCurrentStack(), new Random().nextInt(cardCount), true);
                break;
            default:
                navigationManager.goCard(context, context.getCurrentStack(), ordinal.intValue() - 1, true);
                break;
        }
    }
}
