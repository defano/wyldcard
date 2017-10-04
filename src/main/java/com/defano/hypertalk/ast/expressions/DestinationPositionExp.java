package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.DestinationType;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.Position;
import com.defano.hypertalk.ast.specifiers.PartPositionSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class DestinationPositionExp extends DestinationExp {

    private Position position;
    private DestinationType type;

    public DestinationPositionExp(Position position, DestinationType type) {
        this.type = type;
        this.position = position;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        return new PartPositionSpecifier(Owner.STACK, type.asPartType(), position);
    }
}
