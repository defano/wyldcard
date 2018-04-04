package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.PartToolContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectEmptyCmd extends Command {

    public SelectEmptyCmd(ParserRuleContext context) {
        super(context, "select");
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        ThreadUtils.invokeAndWaitAsNeeded(() -> PartToolContext.getInstance().deselectAllParts());
    }
}
