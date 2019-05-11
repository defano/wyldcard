package com.defano.hypertalk.ast.expression.part;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.model.specifier.WindowIdSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class WindowIdExp extends PartExp {

    private final Expression windowIdExpression;

    public WindowIdExp(ParserRuleContext context, Expression windowIdExpression) {
        super(context);
        this.windowIdExpression = windowIdExpression;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException {
        return new WindowIdSpecifier(windowIdExpression.evaluate(context).integerValue());
    }
}
