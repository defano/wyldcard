package com.defano.hypertalk.ast.expression.part;

import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class LiteralPartExp extends PartExp {

    private final PartSpecifier specifier;

    public LiteralPartExp(ParserRuleContext ctx, PartSpecifier ps) {
        super(ctx);
        this.specifier = ps;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return specifier;
    }
}
