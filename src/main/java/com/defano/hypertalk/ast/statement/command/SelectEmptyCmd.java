package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.hypertalk.ast.statement.Command;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectEmptyCmd extends Command {

    public SelectEmptyCmd(ParserRuleContext context) {
        super(context, "select");
    }

    @Override
    public void onExecute(ExecutionContext context) {
        Invoke.onDispatch(() -> WyldCard.getInstance().getPartToolManager().deselectAllParts());
    }
}
