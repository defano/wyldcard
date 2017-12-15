package com.defano.hypercard.parts.finder;

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
public interface LayeredPartFinder extends PartFinder {

    /**
     * Gets a collection of all parts (buttons, fields) that are searchable.
     * @return The collection of parts in this container.
     */
    Collection<PartModel> getPartModels();
    
    /**
     * Gets the list of parts returned by {@link #getPartModels(), sorted by their z-order (the order in which one is
     * drawn atop another). If {@link #getPartModels()} returns {@link com.defano.hypercard.parts.card.CardModel}
     * objects, they will be appended to the end of the list, after any button or field models.
     *
     * @return The z-ordered list of parts on this card.
     */
    default List<PartModel> getPartsInDisplayOrder() {
        ArrayList<PartModel> allParts = new ArrayList<>();

        ArrayList<PartModel> bkgndParts = new ArrayList<>(getPartsInDisplayOrder(Owner.BACKGROUND));
        bkgndParts.sort(new ZOrderComparator());

        ArrayList<PartModel> cardParts = new ArrayList<>(getPartsInDisplayOrder(Owner.CARD));
        cardParts.sort(new ZOrderComparator());

        allParts.addAll(bkgndParts);
        allParts.addAll(cardParts);

        allParts.addAll(getCardPartsInDisplayOrder());

        return allParts;
    }

    /**
     * Gets the list of cards present in {@link #getPartModels()} returned in the provided order.
     * @return The list of cards present in {@link #getPartModels()}.
     */
    default List<PartModel> getCardPartsInDisplayOrder() {
        return getPartModels().stream()
                .filter(p -> p.getType() == PartType.CARD)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of buttons and field in the list returned by {@link #getPartModels()} that appear on a given layer,
     * listed in their z-order (that is, the order in which one is drawn atop another).
     *
     * @param layer The layer of parts to be returned
     * @return The z-ordered list of parts in the given layer of this card.
     */
    default List<PartModel> getPartsInDisplayOrder(Owner layer) {
        return getPartModels()
                .stream()
                .filter(p -> layer == null || p.getOwner() == layer)
                .filter(p -> p.getType() == PartType.BUTTON || p.getType() == PartType.FIELD)
                .sorted(new ZOrderComparator())
                .collect(Collectors.toList());
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
