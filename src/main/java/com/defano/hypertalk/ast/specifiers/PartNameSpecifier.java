package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;


public class PartNameSpecifier implements PartSpecifier {

    public final Owner layer;
    public final PartType type;
    public final String name;

    public PartNameSpecifier (Owner layer, PartType type, String name) {
        this.layer = layer;
        this.type = type;
        this.name = name;
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
    public String value() {
        return name;
    }

    @Override
    public String getHyperTalkIdentifier() {
        return owner().name().toLowerCase() + " " + type.toString().toLowerCase() + " " + name;
    }
}
