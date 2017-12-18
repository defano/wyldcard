package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PopCardCmd extends Command {

    public PopCardCmd(ParserRuleContext context) {
        super(context, "pop");
    }

    @Override
    protected void onExecute() throws HtException, Breakpoint {
        HyperCard.getInstance().getStack().popCard(null);
    }
}
