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

    @Override
    public String getHyperTalkIdentifier() {
        if (layer == null) {
            return position.name().toLowerCase() + " " + type.toString().toLowerCase();
        } else if (type == null) {
            return position.name().toLowerCase() + " " + owner().name() + " part";
        } else {
            return position.name().toLowerCase() + " " + owner().name() + " " + type.toString().toLowerCase();
        }
    }
}
