package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.finder.OrderedPartFindingSpecifier;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;

/**
 * Specifies the one-and-only message box (aka message, aka message window).
 */
public class PartMessageSpecifier implements PartSpecifier, OrderedPartFindingSpecifier {

    @Override
    public PartModel findSpecifiedPart(ExecutionContext context, List<PartModel> parts) {
        return WyldCard.getInstance().getWindowManager().getMessageWindow().getPartModel();
    }

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
