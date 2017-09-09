package com.defano.hypercard.parts;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Position;
import com.defano.hypertalk.ast.containers.*;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface PartContainer {

    /**
     * Gets all parts in this container in the order that they appear or are displayed. For buttons and fields, this
     * is their z-order; for card or backgrounds, this is their order in the stack.
     *
     * @return The list of parts held by this container in their logical displayed order.
     */
    List<PartModel> getPartsInDisplayOrder();

    /**
     * Returns the part represented by the given part specifier.
     *
     * @param ps The part specifier representing the part to fetch
     * @return The specified part
     * @throws PartException Thrown if no such part exists on this card.
     */
    default PartModel findPart(PartSpecifier ps) throws PartException {
        if (ps instanceof PartIdSpecifier) {
            return findPartById((PartIdSpecifier) ps);
        } else if (ps instanceof PartNameSpecifier) {
            return findPartByName((PartNameSpecifier) ps);
        } else if (ps instanceof PartNumberSpecifier) {
            return findPartByNumber((PartNumberSpecifier) ps);
        } else if (ps instanceof PartOrdinalSpecifier) {
            return findPartByOrdinal((PartOrdinalSpecifier) ps);
        } else if (ps instanceof PartPositionSpecifier) {
            return findPartByPosition((PartPositionSpecifier) ps);
        }

        throw new IllegalArgumentException("Bug! Unimplemented PartSpecifier: " + ps);
    }

    /**
     * Finds a part based on its ID.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default PartModel findPartById(PartIdSpecifier ps) throws PartException {
        Optional<PartModel> foundPart = getPartsInDisplayOrder().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.owner() == null || p.getOwner() == ps.owner())
                .filter(p -> p.getId() == ps.id)
                .findFirst();

        if (foundPart.isPresent()) {
            return foundPart.get();
        } else {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this " + ps.layer.friendlyName.toLowerCase() + ".");
        }
    }

    /**
     * Finds a part based on its name.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default PartModel findPartByName(PartNameSpecifier ps) throws PartException {
        Optional<PartModel> foundPart = getPartsInDisplayOrder().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.owner() == null || p.getOwner() == ps.owner())
                .filter(p -> p.getName().equalsIgnoreCase(ps.value()))
                .findFirst();

        if (foundPart.isPresent()) {
            return foundPart.get();
        } else {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this " + ps.layer.friendlyName.toLowerCase() + ".");
        }
    }

    /**
     * Finds a part based on its number.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default PartModel findPartByNumber(PartNumberSpecifier ps) throws PartException {
        List<PartModel> foundParts = getPartsInDisplayOrder().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.owner() == null || p.getOwner() == ps.owner())
                .collect(Collectors.toList());

        int partIndex = ps.number - 1;

        if (partIndex >= foundParts.size() || partIndex < 0) {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this " + ps.layer.friendlyName.toLowerCase() + ".");
        } else {
            return foundParts.get(partIndex);
        }
    }

    /**
     * Finds a part based on ordinal (first, second... middle, last).
     *
     * @param ps The specification of the part to find
     * @return The specified part
     * @throws PartException Thrown if no part can by found matching the specifier.
     */
    default PartModel findPartByOrdinal(PartOrdinalSpecifier ps) throws PartException {
        List<PartModel> foundParts = getPartsInDisplayOrder().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.owner() == null || p.getOwner() == ps.owner())
                .collect(Collectors.toList());

        int index = ps.ordinal.intValue() - 1;

        if (ps.ordinal == Ordinal.LAST) {
            index = foundParts.size() - 1;
        } else if (ps.ordinal == Ordinal.MIDDLE) {
            index = foundParts.size() / 2;
        }

        if (index < 0 || index >= foundParts.size()) {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this " + ps.layer.friendlyName.toLowerCase() + ".");
        } else {
            return foundParts.get(index);
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
        if (ps.type() != PartType.BACKGROUND && ps.type() != PartType.CARD) {
            throw new PartException("Cannot find " + ps.type().toString().toLowerCase() + " by position.");
        }

        int thisCard = HyperCard.getInstance().getCard().getCardIndexInStack();

        try {
            if (ps.type() == PartType.CARD) {
                switch ((Position) ps.value()) {
                    case NEXT:
                        return HyperCard.getInstance().getStack().getStackModel().getCardModel(thisCard + 1);
                    case PREV:
                        return HyperCard.getInstance().getStack().getStackModel().getCardModel(thisCard - 1);
                    case THIS:
                        return HyperCard.getInstance().getStack().getStackModel().getCardModel(thisCard);
                }
            }

            if (ps.type() == PartType.BACKGROUND) {
                switch ((Position) ps.value()) {
                    case NEXT:
                        return HyperCard.getInstance().getStack().getStackModel().getBackground(findNextBackground().getBackgroundId());
                    case PREV:
                        return HyperCard.getInstance().getStack().getStackModel().getBackground(findPrevBackground().getBackgroundId());
                    case THIS:
                        return HyperCard.getInstance().getCard().getCardBackground();
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
        int thisCard = HyperCard.getInstance().getCard().getCardIndexInStack();
        List<CardModel> prevCards = Lists.reverse(HyperCard.getInstance().getStack().getStackModel().getCardModels().subList(0, thisCard + 1));

        return findNextBackground(prevCards);
    }

    /**
     * Finds the next card containing a different background then the current card.
     * @return The next card with a different background than the current card.
     * @throws PartException Thrown if no such card can be found.
     */
    default CardModel findNextBackground() throws PartException {
        int thisCard = HyperCard.getInstance().getCard().getCardIndexInStack();
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
