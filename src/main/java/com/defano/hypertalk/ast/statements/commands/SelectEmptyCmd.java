package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.hypertalk.ast.statements.Command;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectEmptyCmd extends Command {

    public SelectEmptyCmd(ParserRuleContext context) {
        super(context, "select");
    }

    @Override
    public void onExecute(ExecutionContext context) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> WyldCard.getInstance().getPartToolManager().deselectAllParts());
    }
}
