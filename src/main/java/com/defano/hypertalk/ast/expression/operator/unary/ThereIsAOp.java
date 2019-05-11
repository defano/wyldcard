package com.defano.hypertalk.ast.expression.operator.unary;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.container.MenuExp;
import com.defano.hypertalk.ast.expression.container.MenuItemExp;
import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.expression.factor.FactorAction;
import com.defano.hypertalk.ast.expression.factor.FactorAssociation;
import com.defano.hypertalk.ast.expression.operator.UnaryOperatorExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThereIsAOp extends UnaryOperatorExp {

    public ThereIsAOp(ParserRuleContext context, Expression rhs) {
        super(context, rhs);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Value onEvaluate(ExecutionContext context) {
        AtomicBoolean found = new AtomicBoolean(false);

        try {
            rhs.factor(context,
                    // Looking for the existence of a menu
                    new FactorAssociation(MenuExp.class, (FactorAction<MenuExp>) factor -> {
                        if (factor.menu.exists(context))
                            found.set(true);
                    }),

                    // Looking for the existence of a menu item
                    new FactorAssociation(MenuItemExp.class, (FactorAction<MenuItemExp>) factor -> {
                        if (factor.item.exists(context)) {
                            found.set(true);
                        }
                    }),

                    // Looking for the existence of a part (button, field, card, bg, stack or window)
                    new FactorAssociation(PartExp.class, (FactorAction<PartExp>) factor -> {
                        try {
                            context.getPart(factor.evaluateAsSpecifier(context));
                            found.set(true);
                        } catch (HtNoSuchPartException e) {
                            // Not found
                        }
                    })
            );
        } catch (HtException e) {
            throw new IllegalStateException("Bug! Unexpected exception while determining existence.", e);
        }

        return new Value(found.get());
    }
}
