/*
 * CardModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypercard.Serializer;
import com.defano.hypercard.parts.Part;
import com.defano.hypercard.parts.PartException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A data model representing a card in a stack. See {@link com.defano.hypercard.parts.CardPart} for the associated
 * view object.
 */
public class CardModel {

    private int cardId = 0;
    private int backgroundId = 0;
    private Collection<ButtonModel> buttonModels;
    private Collection<FieldModel> fieldModels;
    private byte[] cardImage;

    private CardModel (int cardId, int backgroundId) {
        this.buttonModels = new ArrayList<>();
        this.fieldModels = new ArrayList<>();
        this.backgroundId = backgroundId;
        this.cardId = cardId;
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
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            this.cardImage = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to save the card image.", e);
        }
    }

    /**
     * Returns the image of this card's foreground.
     *
     * @return The foreground image.
     */
    public BufferedImage getCardImage() {
        if (cardImage == null || cardImage.length == 0) {
            return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        } else {
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(cardImage);
                return ImageIO.read(stream);
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while reading the card image. The stack may be corrupted.", e);
            }
        }
    }

    /**
     * Create's a deep copy of this card.
     * @return A copy of this card.
     */
    public CardModel copyOf() {
        return Serializer.copy(this);
    }
}
