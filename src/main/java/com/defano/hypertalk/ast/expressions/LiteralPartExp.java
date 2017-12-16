package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import org.antlr.v4.runtime.ParserRuleContext;

public class LiteralPartExp extends PartContainerExp {

    private final PartSpecifier specifier;

    public LiteralPartExp(ParserRuleContext ctx, PartSpecifier ps) {
        super(ctx);
        this.specifier = ps;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() {
        return specifier;
    }
}
