package com.defano.hypercard.parts;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.util.ZOrderComparator;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A container of button and field parts. Provides mixin functionality for finding, retrieving and counting parts.
 */
public interface LayeredPartContainer extends PartContainer {

    /**
     * Gets a collection of all parts (buttons and fields) held by this container.
     * @return The collection of parts in this container.
     */
    Collection<PartModel> getPartModels();

    /**
     * Gets a list of parts (buttons and field) that appear on this card, listed in their z-order (that is, the order
     * in which one is drawn atop another).
     *
     * @return The z-ordered list of parts on this card.
     */
    default List<PartModel> getPartsInDisplayOrder() {
        ArrayList<PartModel> bkgndParts = new ArrayList<>(getPartsInDisplayOrder(Owner.BACKGROUND));
        bkgndParts.sort(new ZOrderComparator());

        ArrayList<PartModel> cardParts = new ArrayList<>(getPartsInDisplayOrder(Owner.CARD));
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
    default List<PartModel> getPartsInDisplayOrder(Owner layer) {

        ArrayList<PartModel> parts = getPartModels()
                .stream()
                .filter(p -> p.getOwner() == layer)
                .sorted(new ZOrderComparator())
                .collect(Collectors.toCollection(ArrayList::new));

        return parts;
    }

    /**
     * Gets the number of parts of the given type that exist on the specified layer.
     *
     * @param type Type of part to count or null to count all parts
     * @return The number of parts of the given type displayed on this card.
     */
    default long getPartCount(PartType type, Owner layer) {
        return getPartsInDisplayOrder(layer)
                .stream()
                .filter(p -> type == null || p.getType() == type)
                .count();
    }
}
