package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.MenuContainer;
import com.defano.hypertalk.ast.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class MenuItemExp extends Expression {

    public final MenuItemSpecifier menuItemSpecifier;

    public MenuItemExp(ParserRuleContext context, MenuItemSpecifier menuItemSpecifier) {
        super(context);
        this.menuItemSpecifier = menuItemSpecifier;
    }

    @Override
    protected Value onEvaluate() throws HtException {
        return new MenuContainer(menuItemSpecifier).getValue();
    }

}
