package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.common.PartLayer;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartNumberSpecifier;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class PartNumberExp extends PartExp {

    public final PartLayer layer;
    public final PartType type;
    public final Expression number;

    public PartNumberExp(PartLayer layer, Expression number) {
        this(layer, null, number);
    }

    public PartNumberExp(PartLayer layer, PartType type, Expression number) {
        this.layer = layer;
        this.number = number;
        this.type = type;
    }

    public Value evaluate() throws HtSemanticException {
        try {
            return GlobalContext.getContext().get(evaluateAsSpecifier()).getValue();
        } catch (Exception e) {
            throw new HtSemanticException("Can't get that part.");
        }
    }

    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        return new PartNumberSpecifier(layer, number.evaluate().integerValue());
    }
}
