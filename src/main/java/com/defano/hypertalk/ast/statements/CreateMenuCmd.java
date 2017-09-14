package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class CreateMenuCmd extends Command {

    private final Expression menuName;

    public CreateMenuCmd(Expression menuName) {
        super("create");
        this.menuName = menuName;
    }

    @Override
    public void onExecute() throws HtException {
        HyperCardMenuBar.instance.createMenu(menuName.evaluate().stringValue());
    }
}
