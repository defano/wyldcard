package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PopCardCmd extends Command {

    public PopCardCmd(ParserRuleContext context) {
        super(context, "pop");
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        WyldCard.getInstance().getActiveStack().popCard(context, null);
    }
}
