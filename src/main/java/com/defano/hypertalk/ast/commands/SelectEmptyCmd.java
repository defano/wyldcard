package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.context.PartToolContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;

public class SelectEmptyCmd extends Command {

    public SelectEmptyCmd() {
        super("select");
    }

    @Override
    public void onExecute() throws HtException {
        ThreadUtils.invokeAndWaitAsNeeded(() -> PartToolContext.getInstance().deselectAllParts());
    }
}
