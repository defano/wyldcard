package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.WyldCardFrame;

import java.util.List;

public abstract class WindowSpecifier implements PartSpecifier {

    public abstract WyldCardFrame find(ExecutionContext context, List<WyldCardFrame> windows) throws HtNoSuchPartException;

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
