package com.defano.hypercard.parts;

import com.defano.hypercard.parts.card.CardLayer;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.specifiers.*;

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

    default PartModel findPart(PartSpecifier ps) throws PartException {
        return findPart(ps, getPartsInDisplayOrder());
    }

    /**
     * Returns the part represented by the given part specifier within the ordered collection of parts.
     *
     * @param ps The part specifier representing the part to fetch
     * @return The specified part
     * @throws PartException Thrown if no such part exists on this card.
     */
    default PartModel findPart(PartSpecifier ps, List<PartModel> parts) throws PartException {
        if (ps instanceof PartIdSpecifier) {
            return findPartById((PartIdSpecifier) ps, parts);
        } else if (ps instanceof PartNameSpecifier) {
            return findPartByName((PartNameSpecifier) ps, parts);
        } else if (ps instanceof PartNumberSpecifier) {
            return findPartByNumber((PartNumberSpecifier) ps, parts);
        } else if (ps instanceof PartOrdinalSpecifier) {
            return findPartByOrdinal((PartOrdinalSpecifier) ps, parts);
        } else if (ps instanceof PartMessageSpecifier) {
            return WindowManager.getMessageWindow().getPartModel();
        }

        throw new IllegalArgumentException("Bug! Unimplemented PartSpecifier: " + ps);
    }

    default long getPartNumber(PartModel part) {
        return getPartNumber(part, getPartsInDisplayOrder());
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
    default long getPartNumber(PartModel part, List<PartModel> parts) {
        int number = 0;

        for (PartModel thisPart : parts) {
            number++;
            if (thisPart.getId() == part.getId()) {
                return number;
            }
        }

        throw new IllegalArgumentException("No such part on this card.");
    }

    /**
     * Finds a part based on its ID.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default PartModel findPartById(PartIdSpecifier ps, List<PartModel> parts) throws PartException {
        Optional<PartModel> foundPart = parts.stream()
                .filter(p -> ps.getType() == null || p.getType() == ps.getType())
                .filter(p -> ps.getOwner() == null || p.getOwner() == ps.getOwner())
                .filter(p -> p.getId() == ps.getValue())
                .findFirst();

        if (foundPart.isPresent()) {
            return foundPart.get();
        } else {
            throw new PartException("No " + ps.getHyperTalkIdentifier() + " found.");
        }
    }

    /**
     * Finds a part based on its name.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default PartModel findPartByName(PartNameSpecifier ps, List<PartModel> parts) throws PartException {
        Optional<PartModel> foundPart = parts.stream()
                .filter(p -> ps.getType() == null || p.getType() == ps.getType())
                .filter(p -> ps.getOwner() == null || p.getOwner() == ps.getOwner())
                .filter(p -> p.getName().equalsIgnoreCase(ps.getValue()))
                .findFirst();

        if (foundPart.isPresent()) {
            return foundPart.get();
        } else {
            throw new PartException("No " + ps.getHyperTalkIdentifier() + " found.");
        }
    }

    /**
     * Finds a part based on its number.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default PartModel findPartByNumber(PartNumberSpecifier ps, List<PartModel> parts) throws PartException {
        List<PartModel> foundParts = parts.stream()
                .filter(p -> ps.getType() == null || p.getType() == ps.getType())
                .filter(p -> ps.getOwner() == null || p.getOwner() == ps.getOwner())
                .collect(Collectors.toList());

        int partIndex = (int) ps.getValue() - 1;

        if (partIndex >= foundParts.size() || partIndex < 0) {
            throw new PartException("No " + ps.getHyperTalkIdentifier() + " found.");
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
    default PartModel findPartByOrdinal(PartOrdinalSpecifier ps, List<PartModel> parts) throws PartException {
        List<PartModel> foundParts = parts.stream()
                .filter(p -> ps.getType() == null || p.getType() == ps.getType())
                .filter(p -> ps.getOwner() == null || p.getOwner() == ps.getOwner())
                .collect(Collectors.toList());

        int index = ((Ordinal) ps.getValue()).intValue() - 1;

        if (ps.getValue() == Ordinal.LAST) {
            index = foundParts.size() - 1;
        } else if (ps.getValue() == Ordinal.MIDDLE) {
            index = foundParts.size() / 2;
        }

        if (index < 0 || index >= foundParts.size()) {
            throw new PartException("No " + ps.getHyperTalkIdentifier() + " exists on this " + ps.getOwner().hyperTalkName.toLowerCase() + ".");
        } else {
            return foundParts.get(index);
        }
    }

}
