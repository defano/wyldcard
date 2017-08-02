package com.defano.hypercard.parts;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.containers.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface PartContainer {

    List<PartModel> getPartsInDisplayOrder();

    /**
     * Returns the part (button or field) represented by a given a HyperTalk part specifier.
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
    default PartModel findPartById(PartIdSpecifier ps) throws PartException {
        Optional<PartModel> foundPart = getPartsInDisplayOrder().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.layer() == null || p.getOwner() == ps.layer())
                .filter(p -> p.getId() == ps.id)
                .findFirst();

        if (foundPart.isPresent()) {
            return foundPart.get();
        } else {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this " + ps.layer.friendlyName.toLowerCase());
        }
    }

    /**
     * Returns the part (button or field) identified by the given specifier.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default PartModel findPartByName(PartNameSpecifier ps) throws PartException {
        Optional<PartModel> foundPart = getPartsInDisplayOrder().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.layer() == null || p.getOwner() == ps.layer())
                .filter(p -> p.getName().equalsIgnoreCase(ps.value()))
                .findFirst();

        if (foundPart.isPresent()) {
            return foundPart.get();
        } else {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this " + ps.layer.friendlyName.toLowerCase());
        }
    }

    /**
     * Returns the part (button or field) identified by the given specifier.
     *
     * @param ps The specification of the part to find.
     * @return The specified part.
     * @throws PartException Thrown if no part can be found matching the specifier.
     */
    default PartModel findPartByNumber(PartNumberSpecifier ps) throws PartException {
        List<PartModel> foundParts = getPartsInDisplayOrder().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.layer() == null || p.getOwner() == ps.layer())
                .collect(Collectors.toList());

        int partIndex = ps.number - 1;

        if (partIndex >= foundParts.size() || partIndex < 0) {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this " + ps.layer.friendlyName.toLowerCase());
        } else {
            return foundParts.get(partIndex);
        }
    }

    /**
     * Returns the part indentified by the given specifier.
     *
     * @param ps The specification of the part to find
     * @return The specified part
     * @throws PartException Thrown if no part can by found matching the specifier.
     */
    default PartModel findPartByOrdinal(PartOrdinalSpecifier ps) throws PartException {
        List<PartModel> foundParts = getPartsInDisplayOrder().stream()
                .filter(p -> ps.type() == null || p.getType() == ps.type())
                .filter(p -> ps.layer() == null || p.getOwner() == ps.layer())
                .collect(Collectors.toList());

        int index = ps.ordinal.intValue() - 1;

        if (ps.ordinal == Ordinal.LAST) {
            index = foundParts.size() - 1;
        } else if (ps.ordinal == Ordinal.MIDDLE) {
            index = foundParts.size() / 2;
        }

        if (index < 0 || index >= foundParts.size()) {
            throw new PartException("No " + ps.toString().toLowerCase() + " exists on this " + ps.layer.friendlyName.toLowerCase());
        } else {
            return foundParts.get(index);
        }
    }
}
