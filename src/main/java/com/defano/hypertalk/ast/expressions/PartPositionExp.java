package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class PartPositionExp extends PartExp {

    private final PartType type;
    private final Position position;

    public PartPositionExp(PartType type, Position position) {
        this.type = type;
        this.position = position;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        return null;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        return null;
    }
}
