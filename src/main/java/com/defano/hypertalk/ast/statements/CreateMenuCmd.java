package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class CreateMenuCmd extends Statement {

    private final Expression menuName;

    public CreateMenuCmd(Expression menuName) {
        this.menuName = menuName;
    }

    @Override
    public void execute() throws HtException {
        HyperCardMenuBar.instance.createMenu(menuName.evaluate().stringValue());
    }
}
