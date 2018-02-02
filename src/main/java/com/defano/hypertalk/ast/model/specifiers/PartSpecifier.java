package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;

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
     * @return The owner of the specified part.
     */
    Owner getOwner();

    /**
     * Gets the type of part being specified. See {@link PartType} for an enumeration of part types.
     * @return The type of the specified part.
     */
    PartType getType();

    /**
     * Determines if this specifier refers to a button or field part.
     * @return True if this specifier specifies a button or field.
     */
    default boolean isButtonOrFieldSpecifier() {
        return getType() == PartType.BUTTON || getType() == PartType.FIELD || getType() == null;
    }

    /**
     * Determines if this specifier refers to a background part.
     * @return True if this specifier specifies a background.
     */
    default boolean isBackgroundPartSpecifier() {
        return isButtonOrFieldSpecifier() && getOwner() == Owner.BACKGROUND;
    }

    /**
     * Determines if this specifier refers to a card part.
     * @return True if this specifier specifies a card.
     */
    default boolean isCardPartSpecifier() {
        return isButtonOrFieldSpecifier() && getOwner() == Owner.CARD;
    }

    /**
     * Gets a syntactically valid HyperTalk expression that identifies the specified part (i.e., "card field id 13").
     * @return A valid HyperTalk expression referring to the specified part.
     */
    String getHyperTalkIdentifier();
}
