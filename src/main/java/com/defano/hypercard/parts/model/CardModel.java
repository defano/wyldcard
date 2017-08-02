/*
 * CardModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypercard.serializer.Serializer;
import com.defano.hypercard.parts.Part;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A data model representing a card in a stack. See {@link com.defano.hypercard.parts.CardPart} for the associated
 * view object.
 */
public class CardModel extends PartModel {

    public final static String PROP_ID = "id";
    public final static String PROP_MARKED = "marked";
    public final static String PROP_CANTDELETE = "cantdelete";
    public final static String PROP_NAME = "name";
    public final static String PROP_CONTENTS = "contents";

    private int backgroundId = 0;
    private Collection<ButtonModel> buttonModels;
    private Collection<FieldModel> fieldModels;
    private byte[] cardImage;

    private CardModel (int cardId, int backgroundId) {
        super(PartType.CARD, Owner.STACK);

        this.buttonModels = new ArrayList<>();
        this.fieldModels = new ArrayList<>();
        this.backgroundId = backgroundId;

        defineProperty(PROP_ID, new Value(cardId), true);
        defineProperty(PROP_MARKED, new Value(false), false);
        defineProperty(PROP_CANTDELETE, new Value(false), false);
        defineProperty(PROP_NAME, new Value(""), false);
        defineProperty(PROP_CONTENTS, new Value(""), false);
    }

    /**
     * Returns a new CardModel containing no parts and and empty foreground canvas but which inherits the
     * specified background.
     *
     * @param backgroundId The ID of the background this card should inherit.
     * @return The new CardModel
     */
    public static CardModel emptyCardModel (int cardId, int backgroundId) {
        return new CardModel(cardId, backgroundId);
    }

    public Collection<PartModel> getPartModels() {
        List<PartModel> partModels = new ArrayList<>();
        partModels.addAll(buttonModels);
        partModels.addAll(fieldModels);
        return partModels;
    }

    /**
     * Removes the specified part (button or field). Has no effect if the part doesn't exist on this card.
     * @param part The part to remove from this card.
     */
    public void removePart (Part part) {
        switch (part.getType()) {
            case BUTTON:
                buttonModels.remove(part.getPartModel());
                break;
            case FIELD:
                fieldModels.remove(part.getPartModel());
                break;
        }
    }

    /**
     * Adds a part (button or field) to this card.
     *
     * @param part The part to add.
     * @throws PartException Thrown if part type is unsupported.
     */
    public void addPart (Part part) throws PartException {
        switch (part.getType()) {
            case BUTTON:
                buttonModels.add((ButtonModel) part.getPartModel());
                break;
            case FIELD:
                fieldModels.add((FieldModel) part.getPartModel());
                break;
            default:
                throw new PartException("Bug! Unsupported part type: " + part.getType());
        }
    }

    /**
     * Gets the background ID of this card.
     *
     * @return The ID of this card's background.
     */
    public int getBackgroundId() {
        return backgroundId;
    }

    /**
     * Sets the image representing this card's foreground graphics.
     *
     * @param image The card image.
     */
    public void setCardImage(BufferedImage image) {
        this.cardImage = Serializer.serializeImage(image);
    }

    /**
     * Returns the image of this card's foreground.
     *
     * @return The foreground image.
     */
    public BufferedImage getCardImage() {
        return Serializer.deserializeImage(this.cardImage);
    }

    /**
     * Create's a deep copy of this card.
     * @return A copy of this card.
     */
    public CardModel copyOf() {
        return Serializer.copy(this);
    }


    /** {@inheritDoc} */
    @Override
    public String getValueProperty() {
        return PROP_CONTENTS;
    }
}
