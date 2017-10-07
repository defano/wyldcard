package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartNumberSpecifier;
import com.defano.hypertalk.ast.specifiers.PartOrdinalSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartNumberExp extends PartExp {

    public final Owner layer;
    public final PartType type;
    public final Expression number;
    public final Ordinal ordinal;

    public PartNumberExp(ParserRuleContext context, Owner owner, PartType type, Ordinal ordinal) {
        this(context, owner, type, null, ordinal);
    }

    public PartNumberExp(ParserRuleContext context, Owner owner, Expression expression) {
        this(context, owner, PartType.CARD, expression, null);
    }

    public PartNumberExp(ParserRuleContext context, PartType type, Ordinal ordinal) {
        this(context, null, type, null, ordinal);
    }

    private PartNumberExp(ParserRuleContext context, Owner layer, PartType type, Expression number, Ordinal ordinal) {
        super(context);
        this.layer = layer;
        this.number = number;
        this.type = type;
        this.ordinal = ordinal;
    }

    public Value onEvaluate() throws HtSemanticException {
        try {
            return ExecutionContext.getContext().get(evaluateAsSpecifier()).getValue();
        } catch (Exception e) {
            throw new HtSemanticException("Can't get that part.");
        }
    }

    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        if (ordinal != null) {
            return new PartOrdinalSpecifier(layer, type, ordinal);
        } else {
            return new PartNumberSpecifier(layer, type, number.evaluate().integerValue());
        }
    }
}
