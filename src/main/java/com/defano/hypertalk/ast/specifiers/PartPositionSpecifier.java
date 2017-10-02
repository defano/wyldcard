package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;

public class PartPositionSpecifier implements PartSpecifier {

    public final Position position;
    public final Owner layer;
    public final PartType type;

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
    public Owner owner() {
        return layer;
    }

    @Override
    public PartType type() {
        return type;
    }
}
