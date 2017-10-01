package com.defano.hypertalk.ast.containers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

public class PartMessageSpecifier implements PartSpecifier {

    @Override
    public Object value() {
        return null;
    }

    @Override
    public Owner owner() {
        return Owner.HYPERCARD;
    }

    @Override
    public PartType type() {
        return PartType.MESSAGE_BOX;
    }
}
