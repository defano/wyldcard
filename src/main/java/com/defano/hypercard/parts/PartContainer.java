package com.defano.hypercard.parts;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.util.ZOrderComparator;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.containers.PartIdSpecifier;
import com.defano.hypertalk.ast.containers.PartNameSpecifier;
import com.defano.hypertalk.ast.containers.PartNumberSpecifier;
import com.defano.hypertalk.ast.containers.PartSpecifier;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A container of button and field parts. Provides mixin functionality for finding, retrieving and counting parts.
 */
public interface PartContainer {

    /**
     * Gets all buttons on the card (foreground and background) in no specific order.
     *
     * @return All buttons on the card.
     */
    Collection<ButtonPart> getButtons();

    /**
     * Gets all fields on the card (foreground and background) in no specific order.
     *
     * @return All fields on the card.
     */
    Collection<FieldPart> getFields();

    /**
     * Given a Swing component, returns the layer of the card on which the component is present. Throws
     * IllegalArgumentException if the component does not exist on the card.
     *
     * @param component The component whose card layer should be determined
     * @return The card layer on which the given component lives.
     */
    CardLayer getCardLayer(Component component);

    /**
     * Gets a collection of all parts (buttons and fields) held by this container.
     * @return The collection of parts in this container.
     */
    default Collection<Part> getParts() {
        Collection<Part> parts = new ArrayList<>();
        parts.addAll(getButtons());
        parts.addAll(getFields());
        return parts;
    }

    /**
     * Gets a list of parts (buttons and field) that appear on this card, listed in their z-order (that is, the order
     * in which one is drawn atop another).
     *
     * @return The z-ordered list of parts on this card.
     */
    default List<Part> getPartsInZOrder() {
        ArrayList<Part> bkgndParts = new ArrayList<>();
        bkgndParts.addAll(getPartsInZOrder(CardLayer.BACKGROUND_PARTS));
        bkgndParts.sort(new ZOrderComparator());

        ArrayList<Part> cardParts = new ArrayList<>();
        cardParts.addAll(getPartsInZOrder(CardLayer.CARD_PARTS));
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
    default Part findPartOnCard(PartType type, int id) {
        return getPartsInZOrder()
                .stream()
                .filter(p -> p.getType() == type && p.getPartModel().getKnownProperty(PartModel.PROP_ID).integerValue() == id).findFirst()
                .orElse(null);
    }

    /**
     * Returns the part (button or field) represented by a given a HyperTalk part specifier.
     *
     * @param ps The part specifier representing the part to fetch
     * @return The specified part
     * @throws PartException Thrown if no such part exists on this card.
     */
    default Part findPart(PartSpecifier ps) throws PartException {
        if (ps instanceof PartIdSpecifier) {
            return findPartById((PartIdSpecifier) ps);
        } else if (ps instanceof PartNameSpecifier) {
            return findPartByName((PartNameSpecifier) ps);
        } else if (ps instanceof PartNumberSpecifier) {
            return findPartByNumber((PartNumberSpecifier) ps);
        }

        throw new IllegalArgumentException("Bug! Unimplemented PartSpecifier: " + ps);
    }

    /**
     * Returns the part (button or field) identified by the given specifier.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default Part findPartById(PartIdSpecifier ps) throws PartException {
        if (ps.type != PartType.BUTTON && ps.type != PartType.FIELD) {
            throw new IllegalArgumentException("Can't find parts of type " + ps.type);
        }

        Optional<Part> foundPart = getParts().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.layer() == null || p.getCardLayer().asPartLayer() == ps.layer())
                .filter(p -> p.getId() == ps.id)
                .findFirst();

        if (foundPart.isPresent()) {
            return foundPart.get();
        } else {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this card.");
        }
    }

    /**
     * Returns the part (button or field) identified by the given specifier.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default Part findPartByName(PartNameSpecifier ps) throws PartException {
        if (ps.type != PartType.BUTTON && ps.type != PartType.FIELD) {
            throw new IllegalArgumentException("Can't find parts of type " + ps.type);
        }

        Optional<Part> foundPart = getParts().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.layer() == null || p.getCardLayer().asPartLayer() == ps.layer())
                .filter(p -> p.getName().equalsIgnoreCase(ps.value()))
                .findFirst();

        if (foundPart.isPresent()) {
            return foundPart.get();
        } else {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this card.");
        }
    }

    /**
     * Returns the part (button or field) identified by the given specifier.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default Part findPartByNumber(PartNumberSpecifier ps) throws PartException {
        if (ps.type != PartType.BUTTON && ps.type != PartType.FIELD) {
            throw new IllegalArgumentException("Can't find parts of type " + ps.type);
        }

        List<Part> foundParts = getPartsInZOrder().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.layer() == null || p.getCardLayer().asPartLayer() == ps.layer())
                .collect(Collectors.toList());

        int partIndex = ps.number - 1;

        if (partIndex >= foundParts.size() || partIndex < 0) {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this card.");
        } else {
            return foundParts.get(partIndex);
        }
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

    /**
     * Notify all parts in this container that they are closing (ostensibly because the container itself is closing).
     */
    default void notifyPartsClosing() {
        for (Part p : getParts()) {
            p.partClosed();
        }
    }

}
