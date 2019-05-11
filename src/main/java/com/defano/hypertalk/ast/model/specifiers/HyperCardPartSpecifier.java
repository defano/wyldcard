package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.wyldcard.runtime.ExecutionContext;

public class HyperCardPartSpecifier implements PartSpecifier {

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Owner getOwner() {
        return null;
    }

    @Override
    public PartType getType() {
        return null;
    }

    @Override
    public String getHyperTalkIdentifier(ExecutionContext context) {
        return null;
    }
}
