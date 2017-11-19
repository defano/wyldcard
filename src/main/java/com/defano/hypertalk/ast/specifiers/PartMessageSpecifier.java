package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

/**
 * Specifies the one-and-only message box (aka message, aka message window).
 */
public class PartMessageSpecifier implements PartSpecifier {

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Owner getOwner() {
        return Owner.HYPERCARD;
    }

    @Override
    public PartType getType() {
        return PartType.MESSAGE_BOX;
    }

    @Override
    public String getHyperTalkIdentifier() {
        return "the message";
    }

    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass();
    }
    
}
