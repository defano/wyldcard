package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.menu.HyperCardMenuBar;
import com.defano.hypertalk.exception.HtException;

public class ResetMenuCmd extends Statement {

    @Override
    public void execute() throws HtException {
        HyperCardMenuBar.instance.reset();
    }
}
