package com.defano.hypertalk.ast.specifiers;

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
    public Object getValue() {
        return position;
    }

    @Override
    public Owner getOwner() {
        return layer;
    }

    @Override
    public PartType getType() {
        return type;
    }

    @Override
    public String getHyperTalkIdentifier() {
        if (layer == null) {
            return position.name().toLowerCase() + " " + type.toString().toLowerCase();
        } else if (type == null) {
            return position.name().toLowerCase() + " " + getOwner().name() + " part";
        } else {
            return position.name().toLowerCase() + " " + getOwner().name() + " " + type.toString().toLowerCase();
        }
    }
}
