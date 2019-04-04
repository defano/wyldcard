package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.runtime.context.ExecutionContext;

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
