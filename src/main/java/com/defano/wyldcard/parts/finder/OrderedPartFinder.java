package com.defano.wyldcard.parts.finder;

import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.card.CardLayer;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;

/**
 * Provides functions for finding parts that exist in some relative order to one another (for example, cards in a stack
 * or buttons in z-order on a card).
 */
public interface OrderedPartFinder {

    /**
     * Gets all parts that should be searched in the order that they appear or are displayed. For buttons and fields,
     * this is their z-order; for cards or backgrounds this is their order in the stack.
     *
     * @param context The execution context.
     * @return The list of parts held by this container in their logical display order.
     */
    List<PartModel> getPartsInDisplayOrder(ExecutionContext context);

    /**
     * Finds any part returned by {@link #getPartsInDisplayOrder(ExecutionContext)} by ID, name, number, or ordinal.
     *
     * @param context The execution context.
     * @param ps      A part specifier indicating the part to find.
     * @return The model of the found part.
     * @throws PartException Thrown if the requested part cannot be located.
     */
    default PartModel findPart(ExecutionContext context, PartSpecifier ps) throws PartException {
        return findPart(context, ps, getPartsInDisplayOrder(context));
    }

    /**
     * Finds any part by ID, name, number, or ordinal within an ordered collection of parts.
     *
     * @param context The execution context.
     * @param ps      The part specifier representing the part to fetch; should a subclass of
     *                {@link FindInCollectionSpecifier} for success.
     * @param parts   The list of parts to search
     * @return The specified part
     * @throws PartException Thrown if no such part exists, or if the specifier refers to a non-ordered part type.
     */
    default PartModel findPart(ExecutionContext context, PartSpecifier ps, List<PartModel> parts) throws PartException {
        PartModel foundPart;

        if (ps instanceof FindInCollectionSpecifier) {
            foundPart = ((FindInCollectionSpecifier) ps).findInCollection(context, parts);
        } else {
            throw new PartException("Can't find that.");
        }

        // Special case: Field needs to be evaluated in the context of the current card
        if (foundPart instanceof CardLayerPartModel) {
            ((CardLayerPartModel) foundPart).setCurrentCardId(context.getCurrentCard().getId(context));
        }

        return foundPart;
    }

    /**
     * Calculates the number of a part relative to all other parts returned by {@link #getPartsInDisplayOrder(ExecutionContext)}.
     *
     * @param context The execution context.
     * @param part    The model of the part whose number should be retrieved.
     * @return The number of the given part.
     */
    default long getPartNumber(ExecutionContext context, PartModel part) {
        return getPartNumber(context, part, getPartsInDisplayOrder(context));
    }

    /**
     * Calculates the number of a part relative to all other parts returned by {@link #getPartsInDisplayOrder(ExecutionContext)} and
     * which match the given part type.
     *
     * @param context The execution context.
     * @param part    The model of the part whose number should be retrieved.
     * @param ofType  The type of part being included in the count.
     * @return The number of the request part and type.
     */
    default long getPartNumber(ExecutionContext context, PartModel part, PartType ofType) {
        int number = 0;
        for (PartModel thisPart : getPartsInDisplayOrder(context)) {
            if (thisPart.getType() == ofType) {
                number++;
            }

            if (thisPart.getId(context) == part.getId(context) && thisPart.getType() == ofType) {
                return number;
            }
        }

        throw new IllegalArgumentException("No such part on this card.");
    }

    /**
     * Gets the "number" of the specified part relative to all other parts in the same layer of a given collection of
     * parts.
     * <p>
     * A part number is, effectively, its z-order on the card. The number is a value between 1 and the value returned
     * by {@link ##getPartCount(PartType, CardLayer)}, inclusively.
     *
     * @param context The execution context.
     * @param part    The part whose number should be returned.
     * @return The number of this part
     */
    default long getPartNumber(ExecutionContext context, PartModel part, List<PartModel> parts) {
        int number = 0;

        for (PartModel thisPart : parts) {
            number++;
            if (thisPart.getId(context) == part.getId(context)) {
                return number;
            }
        }

        throw new IllegalArgumentException("No such part on this card.");
    }

}
