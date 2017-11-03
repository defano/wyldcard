package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.expressions.PartExp;

/**
 * Specifies a button or field part that is not on the current card. For example, 'button 3 of card 19'
 */
public class RemotePartSpecifier implements PartSpecifier {

    private final PartSpecifier partSpecifier;      // The button or field
    private final PartExp ofCardPartExp;            // The card the button or field can be found on

    public RemotePartSpecifier(PartSpecifier partSpecifier, PartExp ofCardPartExp) {
        this.partSpecifier = partSpecifier;
        this.ofCardPartExp = ofCardPartExp;
    }

    public PartExp getRemoteCardPartExp() {
        return ofCardPartExp;
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
