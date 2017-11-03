package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

/**
 * Specifies a button, field, card or background part by ordinal number. For example, 'the second card' or 'the fifth
 * card field'
 */
public class PartOrdinalSpecifier implements PartSpecifier {

    private final PartType type;
    private final Owner layer;
    private final Ordinal ordinal;

    public PartOrdinalSpecifier(Owner layer, PartType type, Ordinal ordinal) {
        this.type = type;
        this.layer = layer;
        this.ordinal = ordinal;
    }

    @Override
    public Object getValue() {
        return ordinal;
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
            return ordinal.stringValue().toLowerCase() + " " + type.toString().toLowerCase();
        } else if (type == null) {
            return ordinal.stringValue().toLowerCase() + " " + getOwner().name().toLowerCase() + " part";
        } else {
            return ordinal.stringValue().toLowerCase() + " " + getOwner().name().toLowerCase() + " " + type.toString().toLowerCase();
        }
    }
}
