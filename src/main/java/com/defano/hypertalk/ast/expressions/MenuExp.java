package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.ast.specifiers.MenuSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.MenuContainer;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class MenuExp extends Expression {

    private final MenuSpecifier menuSpecifier;
    private final MenuItemSpecifier menuItemSpecifier;

    public MenuExp(ParserRuleContext context, MenuSpecifier menuContainer) {
        super(context);
        this.menuSpecifier = menuContainer;
        this.menuItemSpecifier = null;
    }

    public MenuExp(ParserRuleContext context, MenuItemSpecifier menuItemSpecifier) {
        super(context);
        this.menuItemSpecifier = menuItemSpecifier;
        this.menuSpecifier = null;
    }

    @Override
    public Value onEvaluate() throws HtException {
        if (menuSpecifier != null) {
            return new MenuContainer(menuSpecifier).getValue();
        } else {
            return new MenuContainer(menuItemSpecifier).getValue();
        }
    }
}
