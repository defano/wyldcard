package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

public class PartNumberSpecifier implements PartSpecifier {

    public final PartType type;
    public final Owner layer;
    public final int number;

    public PartNumberSpecifier(Owner layer, PartType type, int number) {
        this.layer = layer;
        this.number = number;
        this.type = type;
    }

    @Override
    public Object value() {
        return number;
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
    public String toString() {
        if (layer == null) {
            return type + " " + number;
        } else if (type == null) {
            return owner().name() + " part " + number;
        } else {
            return owner().name() + " " + type + " " + number;
        }
    }
}
