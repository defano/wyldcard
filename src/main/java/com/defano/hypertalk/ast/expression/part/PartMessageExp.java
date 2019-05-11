package com.defano.hypertalk.ast.expression.part;

import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.specifier.PartMessageSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartMessageExp extends PartExp {

    public PartMessageExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return new PartMessageSpecifier();
    }
}
