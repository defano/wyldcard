package com.defano.hypercard.parts;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;
import com.defano.hypertalk.ast.specifiers.PartPositionSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.specifiers.RemotePartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

public interface StackPartContainer extends PartContainer {

    StackModel getStackModel();

    /** {@inheritDoc} */
    @Override
    default PartModel findPart(PartSpecifier ps) throws PartException {
        if (ps instanceof PartPositionSpecifier) {
            return findPartByPosition((PartPositionSpecifier) ps);
        } else if (ps instanceof RemotePartSpecifier) {
            return findRemotePart((RemotePartSpecifier) ps);
        } else {
            return PartContainer.super.findPart(ps);
        }
    }

    default CardPart findRemotePartOwner(RemotePartSpecifier ps) throws PartException {
        try {
            CardModel cardModel = (CardModel) findPart(ps.getRemoteCardPartExp().evaluateAsSpecifier());
            return CardPart.skeletonFromModel(cardModel, getStackModel());
        } catch (HtException e) {
            throw new PartException("Can't find that card.");
        }
    }

    default PartModel findRemotePart(RemotePartSpecifier ps) throws PartException {
        return findRemotePartOwner(ps).findPart(ps.getRemotePartSpecifier());
    }

    /**
     * Finds a card or background based on its relative position to the current card or background.
     *
     * @param ps The specification of the card or background to find
     * @return The model of the requested part.
     * @throws PartException Thrown if the requested part cannot be found.
     */
    default PartModel findPartByPosition(PartPositionSpecifier ps) throws PartException {
        if (ps.getType() != PartType.BACKGROUND && ps.getType() != PartType.CARD) {
            throw new PartException("Cannot find " + ps.getType().toString().toLowerCase() + " by position.");
        }

        int thisCard = ExecutionContext.getContext().getCurrentCard().getCardIndexInStack();

        try {
            if (ps.getType() == PartType.CARD) {
                switch ((Position) ps.getValue()) {
                    case NEXT:
                        return HyperCard.getInstance().getStack().getStackModel().getCardModel(thisCard + 1);
                    case PREV:
                        return HyperCard.getInstance().getStack().getStackModel().getCardModel(thisCard - 1);
                    case THIS:
                        return HyperCard.getInstance().getStack().getStackModel().getCardModel(thisCard);
                }
            }

            if (ps.getType() == PartType.BACKGROUND) {
                switch ((Position) ps.getValue()) {
                    case NEXT:
                        return HyperCard.getInstance().getStack().getStackModel().getBackground(findNextBackground().getBackgroundId());
                    case PREV:
                        return HyperCard.getInstance().getStack().getStackModel().getBackground(findPrevBackground().getBackgroundId());
                    case THIS:
                        return ExecutionContext.getContext().getCurrentCard().getCardBackground();
                }
            }

        } catch (Throwable t) {
            throw new PartException("No such card or background.");
        }

        throw new PartException("Bug! Unhandled search term");
    }

    /**
     * Finds the first previous card containing a different background then the current card.
     * @return The first previous card with a different background than the current card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findPrevBackground() throws PartException {
        int thisCard = ExecutionContext.getContext().getCurrentCard().getCardIndexInStack();
        List<CardModel> prevCards = Lists.reverse(HyperCard.getInstance().getStack().getStackModel().getCardModels().subList(0, thisCard + 1));

        return findNextBackground(prevCards);
    }

    /**
     * Finds the next card containing a different background then the current card.
     * @return The next card with a different background than the current card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findNextBackground() throws PartException {
        int thisCard = ExecutionContext.getContext().getCurrentCard().getCardIndexInStack();
        int cardCount = HyperCard.getInstance().getStack().getCardCountProvider().get();
        List<CardModel> nextCards = HyperCard.getInstance().getStack().getStackModel().getCardModels().subList(thisCard, cardCount);

        return findNextBackground(nextCards);
    }

    /**
     * Finds the next card in a list of card models containing a different background than the first card in this list.
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
}
