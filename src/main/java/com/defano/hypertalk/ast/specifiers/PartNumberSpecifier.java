package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

/**
 * Specifies a button, field, card or background part by cardinal number. For example, 'card 14' or 'button 2'
 */
public class PartNumberSpecifier implements PartSpecifier {

    private final PartType type;
    private final Owner layer;
    private final int number;

    public PartNumberSpecifier(Owner layer, PartType type, int number) {
        this.layer = layer;
        this.number = number;
        this.type = type;
    }

    @Override
    public Object getValue() {
        return number;
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
            return type.toString().toLowerCase() + " " + number;
        } else if (type == null) {
            return getOwner().name().toLowerCase() + " part " + number;
        } else {
            return getOwner().name().toLowerCase() + " " + type.toString().toLowerCase() + " " + number;
        }
    }
}
