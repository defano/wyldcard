package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

public class PartOrdinalSpecifier implements PartSpecifier {

    public final PartType type;
    public final Owner layer;
    public final Ordinal ordinal;

    public PartOrdinalSpecifier(Owner layer, PartType type, Ordinal ordinal) {
        this.type = type;
        this.layer = layer;
        this.ordinal = ordinal;
    }

    @Override
    public Object value() {
        return ordinal;
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
