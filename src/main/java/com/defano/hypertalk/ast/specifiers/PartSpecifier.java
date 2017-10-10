package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

public interface PartSpecifier {
    Object value();
    Owner owner();
    PartType type();

    default boolean isCardElementSpecifier() {
        return type() == PartType.BUTTON || type() == PartType.FIELD;
    }

    default boolean isStackElementSpecifier() {
        return type() == PartType.CARD || type() == PartType.BACKGROUND || type() == PartType.MESSAGE_BOX;
    }

    default boolean isStackSpecifier() {
        return type() == PartType.STACK;
    }

    String getHyperTalkIdentifier();
}
