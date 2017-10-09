package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.DestinationType;
import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.specifiers.PartOrdinalSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DestinationOrdinalExp extends DestinationExp {

    private final DestinationType type;
    private final Ordinal ordinal;

    public DestinationOrdinalExp(ParserRuleContext context, Ordinal ordinal, DestinationType type) {
        super(context);
        this.type = type;
        this.ordinal = ordinal;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        return new PartOrdinalSpecifier(Owner.STACK, type.asPartType(), ordinal);
    }
}
