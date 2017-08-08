package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class DeleteMenuCmd extends Statement {

    private final Expression menuName;

    public DeleteMenuCmd(Expression menuName) {
        this.menuName = menuName;
    }

    @Override
    public void execute() throws HtException {
        HyperCardMenuBar.instance.deleteMenu(menuName.evaluate().stringValue());
    }
}
