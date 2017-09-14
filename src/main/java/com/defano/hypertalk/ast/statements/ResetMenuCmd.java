package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.menu.HyperCardMenuBar;
import com.defano.hypertalk.exception.HtException;

public class ResetMenuCmd extends Command {

    public ResetMenuCmd() {
        super("reset");
    }

    @Override
    public void onExecute() throws HtException {
        HyperCardMenuBar.instance.reset();
    }
}
