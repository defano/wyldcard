package com.defano.hypertalk.ast.expressions.parts;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.HyperCardPartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.wyldcard.runtime.context.ExecutionContext;
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
