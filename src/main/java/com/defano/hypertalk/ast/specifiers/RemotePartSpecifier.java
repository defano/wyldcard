package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.expressions.PartExp;

public class RemotePartSpecifier implements PartSpecifier {

    private final PartSpecifier partSpecifier;
    private final PartExp ofPartExp;

    public RemotePartSpecifier(PartSpecifier partSpecifier, PartExp ofPartExp) {
        this.partSpecifier = partSpecifier;
        this.ofPartExp = ofPartExp;
    }

    public PartExp getLocation() {
        return ofPartExp;
    }

    public PartSpecifier getRemotePartSpecifier() {
        return partSpecifier;
    }

    @Override
    public Object getValue() {
        return partSpecifier.getValue();
    }

    @Override
    public Owner getOwner() {
        return partSpecifier.getOwner();
    }

    @Override
    public PartType getType() {
        return partSpecifier.getType();
    }

    @Override
    public String getHyperTalkIdentifier() {
        return partSpecifier.getHyperTalkIdentifier();
    }
}
