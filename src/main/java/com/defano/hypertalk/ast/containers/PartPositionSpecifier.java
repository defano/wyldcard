package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;

public class PartPositionSpecifier implements PartSpecifier {

    private final Position position;
    private final Owner layer;
    private final PartType type;

    public PartPositionSpecifier(Owner layer, PartType type, Position position) {
        this.position = position;
        this.layer = layer;
        this.type = type;
    }

    @Override
    public Object value() {
        return position;
    }

    @Override
    public Owner layer() {
        return layer;
    }

    @Override
    public PartType type() {
        return type;
    }
}
