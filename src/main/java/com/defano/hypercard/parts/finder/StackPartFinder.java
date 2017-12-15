package com.defano.hypercard.parts.finder;

import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.bkgnd.BackgroundModel;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.specifiers.CompositePartSpecifier;
import com.defano.hypertalk.ast.specifiers.PartPositionSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A mix-in containing methods for locating parts contained in the stack.
 */
public interface StackPartFinder extends PartFinder {

    /**
     * Gets the stack model in which parts should be found.
     *
     * @return The stack model to search for parts.
     */
    StackModel getStackModel();

    /**
     * Finds any kind of part contained in this stack: Buttons and fields (card or background layer), cards and
     * backgrounds.
     *
     * @param ps A {@link PartSpecifier} object describing the part to find.
     * @return The model of the requested part.
     * @throws PartException Thrown if the part cannot be located.
     */
    @Override
    default PartModel findPart(PartSpecifier ps) throws PartException {
        if (ps instanceof CompositePartSpecifier) {
            return findCompositePart((CompositePartSpecifier) ps);
        } else if (ps instanceof PartPositionSpecifier) {
            return findPartByPosition((PartPositionSpecifier) ps);
        } else if (ps.isCardPartSpecifier()) {
            return ExecutionContext.getContext().getCurrentCard().getCardModel().findPart(ps);
        } else if (ps.isBackgroundPartSpecifier()) {
            return ExecutionContext.getContext().getCurrentCard().getCardModel().getBackgroundModel().findPart(ps);
        } else {
            return PartFinder.super.findPart(ps);
        }
    }

    /**
     * Finds a part that's part of another part (for example, 'the second card of the first bg', 'card button 3 of card
     * 4 of background 2').
     *
     * @param ps The composite part specifier.
     * @return The found part.
     * @throws PartException Thrown if the requested part cannot be found.
     */
    default PartModel findCompositePart(CompositePartSpecifier ps) throws PartException {
        try {
            PartModel foundPart;

            // Recursively find the card or background containing the requested part
            PartModel owningPart = findPart(ps.getOwningPartExp().evaluateAsSpecifier());

            // Looking for button or field on the remote background
            if (ps.isBackgroundPartSpecifier()) {
                foundPart = findPart(ps.getPart(), ((CardModel) owningPart).getBackgroundModel().getPartsInDisplayOrder(ps.getOwner()));
            }

            // Looking for button or field on the remote card
            else if (ps.isCardPartSpecifier()) {
                foundPart = findPart(ps.getPart(), ((CardModel) owningPart).getPartsInDisplayOrder());
            }

            // Looking for a card in a remote background
            else {
                foundPart = findPart(ps.getPart(), ((LayeredPartFinder) owningPart).getPartsInDisplayOrder());
            }

            // Special case: Field needs to be evaluated in the context of the requested card
            if (foundPart instanceof FieldModel) {
                ((FieldModel) foundPart).setCurrentCardId(owningPart.getId());
            }

            return foundPart;

        } catch (HtException e) {
            throw new PartException(e);
        }
    }

    /**
     * Finds a card or background based on its relative position to the current card or background.
     *
     * @param ps The specification of the card or background to find
     * @return The model of the requested part.
     * @throws PartException Thrown if the requested part cannot be found.
     */
    default PartModel findPartByPosition(PartPositionSpecifier ps) throws PartException {

        // Bail if request to find any kind of part other than a card or background
        if (ps.getType() != PartType.BACKGROUND && ps.getType() != PartType.CARD) {
            throw new PartException("Cannot find " + ps.getType().toString().toLowerCase() + " by position.");
        }

        int thisCard = getStackModel().getCurrentCard().getCardIndexInStack();

        try {
            if (ps.getType() == PartType.CARD) {
                switch (ps.getPosition()) {
                    case NEXT:
                        return getStackModel().getCardModel(thisCard + 1);
                    case PREV:
                        return getStackModel().getCardModel(thisCard - 1);
                    case THIS:
                        return getStackModel().getCardModel(thisCard);
                }
            }

            if (ps.getType() == PartType.BACKGROUND) {
                switch (ps.getPosition()) {
                    case NEXT:
                        return getStackModel().getBackground(findNextBackground().getBackgroundId());
                    case PREV:
                        return getStackModel().getBackground(findPrevBackground().getBackgroundId());
                    case THIS:
                        return getStackModel().getCurrentCard().getBackgroundModel();
                }
            }

        } catch (Throwable t) {
            throw new PartException("No such " + ps.getType().name().toLowerCase() + ".");
        }

        throw new PartException("Bug! Unhandled positional part type: " + ps.getType());
    }

    /**
     * Finds the first previous card containing a different background then the current card.
     *
     * @return The first previous card with a different background than the current card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findPrevBackground() throws PartException {
        int thisCard = getStackModel().getCurrentCard().getCardIndexInStack();
        List<CardModel> prevCards = Lists.reverse(getStackModel().getCardModels().subList(0, thisCard + 1));

        return findNextBackground(prevCards);
    }

    /**
     * Finds the next card containing a different background then the current card.
     *
     * @return The next card with a different background than the current card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findNextBackground() throws PartException {
        int thisCard = getStackModel().getCurrentCard().getCardIndexInStack();
        int cardCount = getStackModel().getCardCount();
        List<CardModel> nextCards = getStackModel().getCardModels().subList(thisCard, cardCount);

        return findNextBackground(nextCards);
    }

    /**
     * Finds the next card within a list of card models containing a different background than the first card in this
     * list.
     *
     * @param cardList The list of card models to traverse.
     * @return The first card with a different background than the first card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findNextBackground(List<CardModel> cardList) throws PartException {
        int thisBackground = cardList.get(0).getBackgroundId();

        Optional<CardModel> nextBkgnd = cardList.stream().filter(cardModel -> cardModel.getBackgroundId() != thisBackground).findFirst();
        if (nextBkgnd.isPresent()) {
            return nextBkgnd.get();
        }

        throw new PartException("No such card.");
    }

    default CardModel findRemotePartOwner(CompositePartSpecifier ps) throws PartException {
        return findRemoteCollectionOwner(ps).get(0);
    }

    default List<CardModel> findRemoteCollectionOwner(CompositePartSpecifier ps) throws PartException {
        BackgroundModel bkgndModel = ps.getOwningPartExp().partFactor(BackgroundModel.class);
        if (bkgndModel != null) {
            return bkgndModel.getCardModels();
        }

        CardModel cardModel = ps.getOwningPartExp().partFactor(CardModel.class);
        if (cardModel != null) {
            return Collections.singletonList(cardModel);
        }

        throw new PartException("Expected a card or background.");
    }

}
