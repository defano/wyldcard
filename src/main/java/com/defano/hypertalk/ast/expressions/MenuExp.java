package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.MenuSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.ContainerMenu;
import com.defano.hypertalk.exception.HtSemanticException;

public class MenuExp extends Expression {

    private final MenuSpecifier menuSpec;

    public MenuExp(MenuSpecifier menuContainer) {
        this.menuSpec = menuContainer;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        return new ContainerMenu(menuSpec).getValue();
    }
}
