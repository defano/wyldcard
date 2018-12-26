package com.defano.hypertalk.ast.expressions.parts;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.WindowNameSpecifier;
import com.defano.hypertalk.ast.model.specifiers.WindowNumberSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
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
