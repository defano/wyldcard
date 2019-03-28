package com.defano.wyldcard.parts.finder;

import com.defano.hypertalk.ast.model.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.WindowSpecifier;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * Provides methods for finding parts that exist within a specific stack.
 * <p>
 * This finder ignores the "stack" portion of any {@link CompositePartSpecifier} assuming the caller has already
 * identified the specified stack and directed the {@link #findPart(ExecutionContext, PartSpecifier)} call to the
 * correct {@link StackPartFinder}.
 * <p>
 * This finder does not handle requests for {@link WindowSpecifier} types, as these are not stack-owned parts.
 */
public interface StackPartFinder extends OrderedPartFinder {

    /**
     * Gets the stack model in which parts should be found.
     *
     * @return The stack model to search for parts.
     */
    StackModel getStackModel();

    /**
     * Finds any kind of part contained within this stack: Buttons and fields (card or background layer), cards and
     * backgrounds.
     *
     * @param context The execution context.
     * @param ps      A {@link PartSpecifier} object describing the part to find.
     * @return The model of the requested part.
     * @throws PartException Thrown if the part cannot be located.
     */
    @Override
    default PartModel findPart(ExecutionContext context, PartSpecifier ps) throws PartException {
        return ps.findInStack(context, getStackModel());
    }

    /**
     * Finds any kind of part within the set of parts returned by {@link #getPartsInDisplayOrder}.
     *
     * @param context The execution context
     * @param ps      The specifier indicating the part to find
     * @return The found part
     * @throws PartException Thrown if the part does not exist
     */
    default PartModel findPartInDisplayedOrder(ExecutionContext context, PartSpecifier ps) throws PartException {
        return OrderedPartFinder.super.findPart(context, ps);
    }

    /**
     * Finds the card on which a part identified by a given {@link CompositePartSpecifier} lives. When the "owning part"
     * is a background (for example, "button 1 of background 3"), then the first card of that background is returned.
     *
     * @param context The execution context.
     * @param ps      A composite part specifier, the owning card of which should be returned.
     * @return The owning card
     * @throws PartException Thrown if no such part can be found.
     */
    default CardModel findOwningCard(ExecutionContext context, CompositePartSpecifier ps) throws PartException {
        BackgroundModel bkgndModel = ps.getOwningPartExp().partFactor(context, BackgroundModel.class);
        if (bkgndModel != null) {
            return bkgndModel.getCardModels(context).get(0);
        }

        CardModel cardModel = ps.getOwningPartExp().partFactor(context, CardModel.class);
        if (cardModel != null) {
            return cardModel;
        }

        throw new PartException("Expected a card or background.");
    }

}
