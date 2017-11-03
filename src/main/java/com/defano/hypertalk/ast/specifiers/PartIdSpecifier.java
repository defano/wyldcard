package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;


public class PartIdSpecifier implements PartSpecifier {

    private final Owner layer;
    private final PartType type;
    private final int id;

    public PartIdSpecifier(Owner layer, PartType type, int id) {
        this.layer = layer;
        this.type = type;
        this.id = id;
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
    public Integer getValue() {
        return id;
    }

    @Override
    public String getHyperTalkIdentifier () {
        if (getOwner() == null) {
            return type.toString().toLowerCase() + " id " + id;
        } else {
            return getOwner().name().toLowerCase() + " " + type.toString().toLowerCase() + " id " + id;
        }
    }
}
