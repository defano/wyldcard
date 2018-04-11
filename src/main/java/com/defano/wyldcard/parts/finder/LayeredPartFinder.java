package com.defano.wyldcard.parts.finder;

import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.util.ZOrderComparator;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.wyldcard.runtime.context.ExecutionContext;

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
     * @param context The execution context.
     */
    Collection<PartModel> getPartModels(ExecutionContext context);
    
    /**
     * Gets the list of parts returned by {@link #getPartModels(ExecutionContext) , sorted by their z-order (the order in which one is
     * drawn atop another). If {@link #getPartModels(ExecutionContext) } returns {@link com.defano.wyldcard.parts.card.CardModel}
     * objects, they will be appended to the end of the list, after any button or field models.
     *
     * @return The z-ordered list of parts on this card.
     * @param context The execution context.
     */
    default List<PartModel> getPartsInDisplayOrder(ExecutionContext context) {
        ArrayList<PartModel> allParts = new ArrayList<>();

        ArrayList<PartModel> bkgndParts = new ArrayList<>(getPartsInDisplayOrder(context, Owner.BACKGROUND));
        bkgndParts.sort(new ZOrderComparator(context));

        ArrayList<PartModel> cardParts = new ArrayList<>(getPartsInDisplayOrder(context, Owner.CARD));
        cardParts.sort(new ZOrderComparator(context));

        allParts.addAll(bkgndParts);
        allParts.addAll(cardParts);

        allParts.addAll(getCardPartsInDisplayOrder(context));

        return allParts;
    }

    /**
     * Gets the list of cards present in {@link #getPartModels(ExecutionContext)} returned in the provided order.
     * @return The list of cards present in {@link #getPartModels(ExecutionContext)}.
     * @param context The execution context.
     */
    default List<PartModel> getCardPartsInDisplayOrder(ExecutionContext context) {
        return getPartModels(context).stream()
                .filter(p -> p.getType() == PartType.CARD)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of buttons and field in the list returned by {@link #getPartModels(ExecutionContext)} that appear on a given layer,
     * listed in their z-order (that is, the order in which one is drawn atop another).
     *
     *
     * @param context The execution context.
     * @param layer The layer of parts to be returned
     * @return The z-ordered list of parts in the given layer of this card.
     */
    default List<PartModel> getPartsInDisplayOrder(ExecutionContext context, Owner layer) {
        return getPartModels(context)
                .stream()
                .filter(p -> layer == null || p.getOwner() == layer)
                .filter(p -> p.getType() == PartType.BUTTON || p.getType() == PartType.FIELD)
                .sorted(new ZOrderComparator(context))
                .collect(Collectors.toList());
    }

    /**
     * Gets the number of parts of the given type that exist on the specified layer.
     *
     *
     * @param context The execution context.
     * @param type Type of part to count or null to count all parts
     * @return The number of parts of the given type displayed on this card.
     */
    default long getPartCount(ExecutionContext context, PartType type, Owner layer) {
        return getPartsInDisplayOrder(context, layer)
                .stream()
                .filter(p -> type == null || p.getType() == type)
                .count();
    }
}
