package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.expressions.parts.CompositePartExp;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.finder.StackPartFinder;
import com.defano.wyldcard.parts.finder.StackPartFindingSpecifier;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public interface PartSpecifier extends StackPartFindingSpecifier {

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
     *
     * @return The type of the specified part.
     */
    PartType getType();

    /**
     * Determines if this specifier refers to a button or field part.
     *
     * @return True if this specifier specifies a button or field.
     */
    default boolean isButtonOrFieldSpecifier() {
        return getType() == PartType.BUTTON || getType() == PartType.FIELD || getType() == null;
    }

    /**
     * Determines if this specifier refers to a background part.
     *
     * @return True if this specifier specifies a background.
     */
    default boolean isBackgroundPartSpecifier() {
        return isButtonOrFieldSpecifier() && getOwner() == Owner.BACKGROUND;
    }

    /**
     * Determines if this specifier refers to a card part.
     *
     * @return True if this specifier specifies a card.
     */
    default boolean isCardPartSpecifier() {
        return isButtonOrFieldSpecifier() && getOwner() == Owner.CARD;
    }

    /**
     * Gets a syntactically valid HyperTalk expression that identifies the specified part (i.e., "card field id 13").
     *
     * @param context The execution context.
     * @return A valid HyperTalk expression referring to the specified part.
     */
    String getHyperTalkIdentifier(ExecutionContext context);

    @Override
    default PartModel find(ExecutionContext context, StackPartFinder partFinder) throws PartException {
        if (isCardPartSpecifier()) {
            return context.getCurrentCard().getPartModel().findPart(context, this);
        } else if (isBackgroundPartSpecifier()) {
            return context.getCurrentCard().getPartModel().getBackgroundModel().findPart(context, this);
        }

        return partFinder.findPartInDisplayedOrder(context, this);
    }

    /**
     * Traverses the chain of owning parts until we reach the root owner of the part; returns a specifier identifying
     * that part.
     * <p>
     * For example, in the expression 'card 3 of the last background of stack "My Stack"', the root owning part
     * specifier would return a specifier identifying "My Stack".
     *
     * @param context The execution context
     * @return The value of 'this' if this object is not a {@link CompositePartSpecifier}, otherwise, returns the
     * specifier at the root of the composite tree.
     * @throws HtException Thrown if an error occurs evaluating the chain of specifiers
     */
    default PartSpecifier getRootOwningPartSpecifier(ExecutionContext context) throws HtException {
        if (this instanceof CompositePartSpecifier) {
            PartExp owningPart = ((CompositePartSpecifier) this).getOwningPartExp();
            if (owningPart instanceof CompositePartExp) {
                return getRootOwningPartSpecifier(context, owningPart);
            } else {
                return owningPart.evaluateAsSpecifier(context);
            }
        } else {
            return this;
        }
    }

    default PartSpecifier getRootOwningPartSpecifier(ExecutionContext context, PartExp from) throws HtException {
        PartSpecifier specifier = from.evaluateAsSpecifier(context);
        if (specifier instanceof CompositePartSpecifier) {
            return getRootOwningPartSpecifier(context, ((CompositePartSpecifier) specifier).getOwningPartExp());
        } else {
            return specifier;
        }
    }
}
