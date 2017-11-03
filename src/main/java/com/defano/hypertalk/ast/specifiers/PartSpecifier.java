package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.DestinationType;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;
import com.defano.hypertalk.ast.expressions.DestinationExp;
import com.defano.hypertalk.ast.expressions.DestinationPositionExp;
import com.defano.hypertalk.ast.expressions.PartExp;

public interface PartSpecifier {

    /**
     * Gets the "value" of this specification. The exact type returned depends on the type of specification. For
     * example, the type of value of a {@link PartIdSpecifier} is int.
     * @return The value of the specification
     */
    Object getValue();

    /**
     * Gets the owner of the part. Buttons and fields are owned by {@link Owner#CARD} or {@link Owner#BACKGROUND}; cards
     * and backgrounds are owned by {@link Owner#STACK}; stacks, and the message box are owned by
     * {@link Owner#HYPERCARD}.
     * @return The owner of this part.
     */
    Owner getOwner();

    /**
     * Gets the type of part being specified. See {@link PartType} for an enumeration of part types.
     * @return The type of part being specified.
     */
    PartType getType();

//    default PartExp getLocation() {
//        return new DestinationPositionExp(null, Position.THIS, DestinationType.CARD);
//    }

    default boolean isCardElementSpecifier() {
        return getType() == PartType.BUTTON || getType() == PartType.FIELD;
    }

    default boolean isStackElementSpecifier() {
        return getType() == PartType.CARD || getType() == PartType.BACKGROUND || getType() == PartType.MESSAGE_BOX;
    }

    default boolean isStackSpecifier() {
        return getType() == PartType.STACK;
    }

    String getHyperTalkIdentifier();
}
