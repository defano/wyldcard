package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.DestinationType;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartNameSpecifier;
import com.defano.hypertalk.ast.specifiers.PartNumberSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DestinationNumberExp extends DestinationExp {

    private final Expression number;
    private final DestinationType type;

    public DestinationNumberExp(ParserRuleContext context, Expression number, DestinationType type) {
        super(context);
        this.type = type;
        this.number = number;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        Value evaluatedName = number.onEvaluate();

        if (evaluatedName.isInteger()) {
            return new PartNumberSpecifier(Owner.STACK, type.asPartType(), evaluatedName.integerValue());
        } else {
            return new PartNameSpecifier(Owner.STACK, type.asPartType(), evaluatedName.stringValue());
        }
    }
}
