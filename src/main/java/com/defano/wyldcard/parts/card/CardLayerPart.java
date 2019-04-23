package com.defano.wyldcard.parts.card;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.Part;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import javax.swing.*;
import java.util.List;

/**
 * Represents a part that exists on a layer (foreground or background) of the card (i.e., either a button or a field).
 */
public interface CardLayerPart<T extends PartModel> extends Part<T> {

    /**
     * Gets the Swing component associated with this part.
     * @return The Java Swing component.
     */
    JComponent getComponent();

    /**
     * Gets the card on which this part exists.
     * @return The part's parent card.
     */
    CardPart getCard();

    /**
     * Determines the layer of the card on which this part exists.
     * @return The layer of the card the part is on or null if indeterminate
     */
    default CardLayer getCardLayer() {
        CardPart card = getCard();
        return card == null ? null : card.getCardLayer(getComponent());
    }

    /**
     * Determines the currently active part layer, either {@link CardLayer#BACKGROUND_PARTS} or
     * {@link CardLayer#CARD_PARTS} depending on whether the user is presently editing the background.
     *
     * @return The part layer currently being edited.
     */
    static CardLayer getActivePartLayer() {
        return WyldCard.getInstance().getToolsManager().isEditingBackground() ? CardLayer.BACKGROUND_PARTS : CardLayer.CARD_PARTS;
    }

    /**
     * Sets the z-position of this part relative to other parts on the card.
     * @param context The execution context.
     * @param newPosition The z-order position of this part
     */
    default void setDisplayOrder(ExecutionContext context, int newPosition) {
        CardPart card = getCard();

        PartModel owner = getCardLayer() == CardLayer.BACKGROUND_PARTS ?
                getCard().getPartModel().getBackgroundModel() :
                getCard().getPartModel();

        List<PartModel> parts = ((LayeredPartFinder) owner).getPartsInDisplayOrder(context);
                // = card.getPartModel().getPartsInDisplayOrder(context, getCardLayer() == CardLayer.BACKGROUND_PARTS ? Owner.BACKGROUND : Owner.CARD);

        if (newPosition < 0) {
            newPosition = 0;
        } else if (newPosition > parts.size() - 1) {
            newPosition = parts.size() - 1;
        }

        parts.remove(getPartModel());
        parts.add(newPosition, getPartModel());

        for (int index = 0; index < parts.size(); index++) {
            PartModel thisPart = parts.get(index);
            if (thisPart instanceof CardLayerPartModel) {
                thisPart.setQuietly(context, CardLayerPartModel.PROP_ZORDER, new Value(index));
            }
        }

        card.invalidatePartsZOrder(context);
    }
}
