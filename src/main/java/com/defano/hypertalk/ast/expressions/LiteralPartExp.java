package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.wyldcard.runtime.context.ExecutionContext;
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
