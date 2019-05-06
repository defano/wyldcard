package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class CloseCardWindowCmd extends Command {

    public CloseCardWindowCmd(ParserRuleContext context) {
        super(context, "close");
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        if (WyldCard.getInstance().getStackManager().getOpenStacks().size() > 1) {
            WyldCard.getInstance().getStackManager().closeStack(context, context.getCurrentStack());
        }
    }
}
