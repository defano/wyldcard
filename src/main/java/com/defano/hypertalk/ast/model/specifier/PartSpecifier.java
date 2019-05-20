package com.defano.hypertalk.ast.model.specifier;

import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.expression.part.CompositePartExp;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.WyldCard;
import com.defano.hypertalk.exception.HtNoSuchPartException;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.part.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface PartSpecifier {

    /**
     * Gets the "value" of this specification. The exact type returned and its meaning depends on the type of
     * specification. For example, the value returned by {@link PartIdSpecifier} is an int representing the integer
     * ID of the part, the value of a {@link PartNameSpecifier} is the String-typed name of the part.
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
    default boolean isSpecifyingButtonOrField() {
        return getType() == PartType.BUTTON || getType() == PartType.FIELD || getType() == null;
    }

    /**
     * Determines if this specifier refers to a background part.
     *
     * @return True if this specifier specifies a background.
     */
    default boolean isSpecifyingBackgroundPart() {
        return isSpecifyingButtonOrField() && getOwner() == Owner.BACKGROUND;
    }

    /**
     * Determines if this specifier refers to a card part.
     *
     * @return True if this specifier specifies a card.
     */
    default boolean isSpecifyingCardPart() {
        return isSpecifyingButtonOrField() && getOwner() == Owner.CARD;
    }

    /**
     * Determines if this specifier refers to a window.
     *
     * @return True if this specifier specifies a window.
     */
    default boolean isSpecifyingWindow() {
        return this instanceof WindowSpecifier;
    }

    /**
     * Determines if this specifier refers to a stack.
     *
     * @return True if this specifier specifies a stack.
     */
    default boolean isSpecifyingStack() {
        return this instanceof StackPartSpecifier;
    }

    /**
     * Determines if this specifier refers to the message window (message box).
     *
     * @return True if this specifier specifies the message.
     */
    default boolean isSpecifyingMessageBox() {
        return this instanceof PartMessageSpecifier;
    }

    default boolean isSpecifyingHyperCard() {
        return this instanceof HyperCardPartSpecifier;
    }

    /**
     * Gets a syntactically valid HyperTalk expression that identifies the specified part (i.e., "card field id 13").
     *
     * @param context The execution context.
     * @return A valid HyperTalk expression referring to the specified part.
     */
    String getHyperTalkIdentifier(ExecutionContext context);

    /**
     * Attempts to find the part identified by this specifier in a given stack.
     *
     * @param context    The execution context
     * @param stackModel The model of the stack to look within
     * @return The found stack model
     * @throws HtNoSuchPartException Thrown if the part cannot be found.
     */
    default PartModel findInStack(ExecutionContext context, StackModel stackModel) throws HtNoSuchPartException {
        if (isSpecifyingCardPart()) {
            return context.getCurrentCard().getPartModel().findPart(context, this);
        } else if (isSpecifyingBackgroundPart()) {
            return context.getCurrentCard().getPartModel().getBackgroundModel().findPart(context, this);
        } else if (isSpecifyingMessageBox()) {
            return WyldCard.getInstance().getWindowManager().getMessageWindow().getPartModel();
        }

        // Not a button or a field that we're looking for...
        return stackModel.findPartInDisplayedOrder(context, this);
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
