package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.wyldcard.runtime.ExecutionContext;

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
    public String getHyperTalkIdentifier(ExecutionContext context) {
        return "the message";
    }

    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass();
    }

    @Override
    public String toString() {
        return "PartMessageSpecifier{}";
    }
}
