package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartPositionSpecifier;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class PartPositionExp extends PartExp {

    private final PartType type;
    private final Position position;

    public PartPositionExp(PartType type, Position position) {
        if (type != PartType.BACKGROUND && type != PartType.CARD) {
            throw new IllegalArgumentException("Cannot specify this type by position: " + type);
        }

        this.type = type;
        this.position = position;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        return new PartPositionSpecifier(Owner.STACK, type, position);
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        try {
            return GlobalContext.getContext().get(evaluateAsSpecifier()).getValue();
        } catch (PartException e) {
            throw new HtSemanticException("Can't get that part.");
        }
    }
}
