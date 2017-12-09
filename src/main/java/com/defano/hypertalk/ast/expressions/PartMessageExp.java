package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartMessageExp extends PartContainerExp {

    public PartMessageExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() {
        return new PartMessageSpecifier();
    }
}
