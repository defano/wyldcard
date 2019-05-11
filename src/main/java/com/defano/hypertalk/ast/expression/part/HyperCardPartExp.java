package com.defano.hypertalk.ast.expression.part;

import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.specifier.HyperCardPartSpecifier;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class HyperCardPartExp extends PartExp {

    public HyperCardPartExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return new HyperCardPartSpecifier();
    }
}
