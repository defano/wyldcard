package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartMessageExp extends PartExp {

    public PartMessageExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() {
        return new PartMessageSpecifier();
    }
}
