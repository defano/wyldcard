package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypercard.runtime.context.PartToolContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectEmptyCmd extends Command {

    public SelectEmptyCmd(ParserRuleContext context) {
        super(context, "select");
    }

    @Override
    public void onExecute() throws HtException {
        ThreadUtils.invokeAndWaitAsNeeded(() -> PartToolContext.getInstance().deselectAllParts());
    }
}
