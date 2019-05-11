package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.NavigationManager;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class PopCardCmd extends Command {

    @Inject
    private NavigationManager navigationManager;

    public PopCardCmd(ParserRuleContext context) {
        super(context, "pop");
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtSemanticException {
        Destination popped = navigationManager.pop();
        if (popped != null) {
            navigationManager.goDestination(context, popped);
        }
    }
}
