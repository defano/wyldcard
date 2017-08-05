package com.defano.hypercard.parts.card;

import com.defano.hypercard.parts.PartContainer;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.util.ZOrderComparator;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A container of button and field parts. Provides mixin functionality for finding, retrieving and counting parts.
 */
public interface LayeredPartContainer extends PartContainer {

    /**
     * Gets a collection of all parts (buttons and fields) held by this container.
     * @return The collection of parts in this container.
     */
    Collection<PartModel> getParts();

    /**
     * Gets a list of parts (buttons and field) that appear on this card, listed in their z-order (that is, the order
     * in which one is drawn atop another).
     *
     * @return The z-ordered list of parts on this card.
     */
    default List<PartModel> getPartsInDisplayOrder() {
        ArrayList<PartModel> bkgndParts = new ArrayList<>();
        bkgndParts.addAll(getPartsInZOrder(Owner.BACKGROUND));
        bkgndParts.sort(new ZOrderComparator());

        ArrayList<PartModel> cardParts = new ArrayList<>();
        cardParts.addAll(getPartsInZOrder(Owner.CARD));
        cardParts.sort(new ZOrderComparator());

        bkgndParts.addAll(cardParts);
        return bkgndParts;
    }

    /**
     * Gets a list of parts (buttons and field) that appear on the given layer of this card, listed in their z-order
     * (that is, the order in which one is drawn atop another).
     *
     * @param layer The layer of parts to be returned
     * @return The z-ordered list of parts in the given layer of this card.
     */
    default List<PartModel> getPartsInZOrder(Owner layer) {
        ArrayList<PartModel> parts = new ArrayList<>();

        parts.addAll(getParts()
                .stream()
                .filter(p -> p.getOwner() == layer)
                .collect(Collectors.toList()));

        parts.sort(new ZOrderComparator());
        return parts;
    }

    /**
     * Finds a part on the card or on the card's background matching the given type and ID.
     *
     * @param type The type of part to find.
     * @param id   The id of the part to find.
     * @return The found part or null if no matching part exists.
     */
    default PartModel findPartOnCard(PartType type, int id) {
        return getPartsInDisplayOrder()
                .stream()
                .filter(p -> p.getType() == type && p.getKnownProperty(PartModel.PROP_ID).integerValue() == id).findFirst()
                .orElse(null);
    }

    /**
     * Gets the number of parts of the given type that exist on the specified layer.
     *
     * @param type Type of part to count or null to count all parts
     * @return The number of parts of the given type displayed on this card.
     */
    default long getPartCount(PartType type, Owner layer) {
        return getPartsInZOrder(layer)
                .stream()
                .filter(p -> type == null || p.getType() == type)
                .count();
    }

    /**
     * Gets the "number" of the specified part on the card relative to all other parts in the same layer.
     * <p>
     * A part number is, effectively, its z-order on the card. The number is a value between 1 and the value returned
     * by {@link ##getPartCount(PartType, CardLayer)}, inclusively.
     *
     * @param part The part whose number should be returned.
     * @return The number of this part
     */
    default long getPartNumber(PartModel part) {
        int number = 0;

        for (PartModel thisPart : getPartsInZOrder(part.getOwner())) {
            number++;
            if (thisPart.getId() == part.getId()) {
                return number;
            }
        }

        throw new IllegalArgumentException("No such part on this card.");
    }

    /**
     * Gets the "number" of the specified button on the card relative to other buttons within the same layer.
     * <p>
     * A part number is, effectively, its z-order on the card. The number is a value between 1 and the value returned
     * by {@link ##getPartCount(PartType, CardLayer)}, inclusively.
     *
     * @param part The part whose number should be returned.
     * @return The number of this part
     */
    default long getButtonNumber(ButtonModel part) {
        int number = 0;
        for (PartModel thisPart : getPartsInZOrder(part.getOwner())) {
            if (thisPart.getType() == PartType.BUTTON) {
                number++;
            }

            if (thisPart.getId() == part.getId()) {
                return number;
            }
        }

        throw new IllegalArgumentException("No such part on this card.");
    }

    /**
     * Gets the "number" of the specified field on the card relative to other buttons within the same layer.
     * <p>
     * A part number is, effectively, its z-order on the card. The number is a value between 1 and the value returned
     * by {@link ##getPartCount(PartType, CardLayer)}, inclusively.
     *
     * @param part The part whose number should be returned.
     * @return The number of this part
     */
    default long getFieldNumber(FieldModel part) {
        int number = 0;
        for (PartModel thisPart : getPartsInZOrder(part.getOwner())) {
            if (thisPart.getType() == PartType.FIELD) {
                number++;
            }

            if (thisPart.getId() == part.getId()) {
                return number;
            }
        }

        throw new IllegalArgumentException("No such part on this card.");
    }

}
