package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

public interface PartSpecifier {

    /**
     * Gets the "value" of this specification. The exact type returned and its meaning depends on the type of
     * specification. For example, the value returned by {@link PartIdSpecifier} is an int representing the integer
     * ID of the part.
     *
     * @return The value of the specification
     */
    Object getValue();

    /**
     * Gets the owner of the part. Buttons and fields are owned by {@link Owner#CARD} or {@link Owner#BACKGROUND}; cards
     * and backgrounds are owned by {@link Owner#STACK}; stacks, and the message box are owned by
     * {@link Owner#HYPERCARD}.
     *
     * @return The owner of this part.
     */
    Owner getOwner();

    /**
     * Gets the type of part being specified. See {@link PartType} for an enumeration of part types.
     * @return The type of part being specified.
     */
    PartType getType();

    default boolean isButtonOrFieldSpecifier() {
        return getType() == PartType.BUTTON || getType() == PartType.FIELD || getType() == null;
    }

    default boolean isBackgroundPartSpecifier() {
        return isButtonOrFieldSpecifier() && getOwner() == Owner.BACKGROUND;
    }

    default boolean isCardPartSpecifier() {
        return isButtonOrFieldSpecifier() && getOwner() == Owner.CARD;
    }

    String getHyperTalkIdentifier();
}
