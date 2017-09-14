package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartNumberSpecifier;
import com.defano.hypertalk.ast.containers.PartOrdinalSpecifier;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class PartNumberExp extends PartExp {

    public final Owner layer;
    public final PartType type;
    public final Expression number;
    public final Ordinal ordinal;

    public PartNumberExp(Owner owner, PartType type, Ordinal ordinal) {
        this(owner, type, null, ordinal);
    }

    public PartNumberExp(Owner owner, Expression expression) {
        this(owner, PartType.CARD, expression, null);
    }

    public PartNumberExp(PartType type, Ordinal ordinal) {
        this(null, type, null, ordinal);
    }

    private PartNumberExp(Owner layer, PartType type, Expression number, Ordinal ordinal) {
        this.layer = layer;
        this.number = number;
        this.type = type;
        this.ordinal = ordinal;
    }

    public Value evaluate() throws HtSemanticException {
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
