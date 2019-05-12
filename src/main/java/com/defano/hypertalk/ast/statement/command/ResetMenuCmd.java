package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.WyldCard;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ResetMenuCmd extends Command {

    public ResetMenuCmd(ParserRuleContext context) {
        super(context, "reset");
    }

    @Override
    public void onExecute(ExecutionContext context) {
        WyldCard.getInstance().getWyldCardMenuBar().reset();
    }
}
