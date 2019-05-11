package com.defano.hypertalk.ast.expression.part;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.model.specifier.WindowNameSpecifier;
import com.defano.hypertalk.ast.model.specifier.WindowNumberSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class WindowNameExp extends PartExp {

    private final Expression windowNameExpression;

    public WindowNameExp(ParserRuleContext context, Expression windowNameExpression) {
        super(context);
        this.windowNameExpression = windowNameExpression;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException {
        Value windowValue = windowNameExpression.evaluate(context);

        if (windowValue.isInteger() && !windowValue.isQuotedLiteral()) {
            return new WindowNumberSpecifier(windowValue.integerValue());
        } else {
            return new WindowNameSpecifier(windowValue.toString());
        }
    }
}
