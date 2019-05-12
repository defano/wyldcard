package com.defano.hypertalk.ast.expression.container;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import org.antlr.v4.runtime.ParserRuleContext;

public class TargetExp extends PartExp {

    public TargetExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return context.getTarget();
    }
}
