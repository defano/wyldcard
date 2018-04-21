package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public abstract class WindowSpecifier implements PartSpecifier {

    @Override
    public Owner getOwner() {
        return Owner.HYPERCARD;
    }

    @Override
    public PartType getType() {
        return PartType.WINDOW;
    }

    @Override
    public String getHyperTalkIdentifier(ExecutionContext context) {
        return "window \'" + getValue() + "\"";
    }
}
