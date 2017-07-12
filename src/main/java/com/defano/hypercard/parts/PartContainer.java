package com.defano.hypercard.parts;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.PartType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface PartContainer {

    /**
     * Gets all buttons on the card (foreground and background) in no specific order.
     * @return All buttons on the card.
     */
    Collection<ButtonPart> getButtons();

    /**
     * Gets all fields on the card (foreground and background) in no specific order.
     * @return All fields on the card.
     */
    Collection<FieldPart> getFields();

    /**
     * Given a Swing component, gets the card layer in which its present
     * @param component
     * @return
     */
    CardLayer getCardLayer(Component component);

    default Comparator<Part> getZOrderComparator() {
        return (o1, o2) -> new Integer(o1.getPartModel().getKnownProperty(PartModel.PROP_ZORDER).integerValue())
                .compareTo(o2.getPartModel().getKnownProperty(PartModel.PROP_ZORDER).integerValue());
    }

    /**
     * Gets a list of parts (buttons and field) that appear on this card, listed in their z-order (that is, the order
     * in which one is drawn atop another).
     * @return The z-ordered list of parts on this card.
     */
    default List<Part> getPartsInZOrder() {
        ArrayList<Part> cardParts = new ArrayList<>();
        cardParts.addAll(getPartsInZOrder(CardLayer.CARD_PARTS));
        cardParts.sort(getZOrderComparator());

        ArrayList<Part> bkgndParts = new ArrayList<>();
        bkgndParts.addAll(getPartsInZOrder(CardLayer.BACKGROUND_PARTS));
        bkgndParts.sort(getZOrderComparator());

        cardParts.addAll(bkgndParts);
        return cardParts;
    }


    /**
     * Gets a list of parts (buttons and field) that appear on the given layer of this card, listed in their z-order
     * (that is, the order in which one is drawn atop another).
     * @param layer The layer of parts to be returned
     * @return The z-ordered list of parts in the given layer of this card.
     */
    default List<Part> getPartsInZOrder(CardLayer layer) {
        ArrayList<Part> parts = new ArrayList<>();

        parts.addAll(getButtons()
                .stream()
                .filter(p -> getCardLayer(p.getComponent()) == layer)
                .collect(Collectors.toList()));

        parts.addAll(getFields()
                .stream()
                .filter(p -> getCardLayer(p.getComponent()) == layer)
                .collect(Collectors.toList()));

        return parts;
    }

    /**
     * Finds a part on the card or on the card's background matching the given type and ID.
     *
     * @param type The type of part to find.
     * @param id   The id of the part to find.
     * @return The found part or null if no matching part exists.
     */
    default Part findPartOnCard(PartType type, int id) {
        return getPartsInZOrder()
                .stream()
                .filter(p -> p.getType() == type && p.getPartModel().getKnownProperty(PartModel.PROP_ID).integerValue() == id).findFirst()
                .orElse(null);
    }

    /**
     * Gets the number of parts of the given type that exist on the specified layer.
     *
     * @param type Type of part to count or null to count all parts
     * @return The number of parts of the given type displayed on this card.
     */
    default long getPartCount(PartType type, CardLayer layer) {
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
    default long getPartNumber(Part part) {
        int number = 0;

        for (Part thisPart : getPartsInZOrder(part.getCardLayer())) {
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
    default long getButtonNumber(ButtonPart part) {
        int number = 0;
        for (Part thisPart : getPartsInZOrder(part.getCardLayer())) {
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
    default long getFieldNumber(FieldPart part) {
        int number = 0;
        for (Part thisPart : getPartsInZOrder(part.getCardLayer())) {
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
