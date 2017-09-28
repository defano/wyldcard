/*
 * CardModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.card;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.serializer.Serializer;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.google.common.collect.Lists;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A data model representing a card in a stack. See {@link CardPart} for the associated
 * view object.
 */
public class CardModel extends PartModel {

    public final static String PROP_ID = "id";
    public final static String PROP_MARKED = "marked";
    public final static String PROP_CANTDELETE = "cantdelete";
    public final static String PROP_NAME = "name";

    private int backgroundId = 0;
    private Collection<FieldModel> fields = new ArrayList<>();
    private Collection<ButtonModel> buttons = new ArrayList<>();
    private byte[] cardImage;

    private CardModel (int cardId, int backgroundId) {
        super(PartType.CARD, Owner.STACK);

        this.backgroundId = backgroundId;

        defineProperty(PROP_ID, new Value(cardId), true);
        defineProperty(PROP_MARKED, new Value(false), false);
        defineProperty(PROP_CANTDELETE, new Value(false), false);
        defineProperty(PROP_NAME, new Value(""), false);
        defineProperty(PROP_CONTENTS, new Value(""), false);

        initialize();
    }

    @Override
    @PostConstruct
    public void initialize() {
        super.initialize();

        // When no name of card is provided, returns 'card id xxx'
        defineComputedGetterProperty(PROP_NAME, (model, propertyName) -> {
            Value raw = model.getRawProperty(propertyName);
            if (raw == null || raw.isEmpty()) {
                return new Value("card id " + model.getKnownProperty(PROP_ID));
            } else {
                return raw;
            }
        });

        // Card inherits size and location properties from the stack
        delegateProperties(Lists.newArrayList(PROP_RECT, PROP_RECTANGLE, PROP_TOP, PROP_LEFT, PROP_BOTTOM, PROP_RIGHT, PROP_TOPLEFT, PROP_BOTTOMRIGHT, PROP_HEIGHT, PROP_WIDTH),
                property -> HyperCard.getInstance().getStack().getStackModel());
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
        return Stream.concat(fields.stream(), buttons.stream()).collect(Collectors.toList());
    }

    /**
     * Removes the specified part (button or field). Has no effect if the part doesn't exist on this card.
     * @param partModel The part to remove from this card.
     */
    public void removePartModel(PartModel partModel) {
        switch (partModel.getType()) {
            case FIELD:
                fields.remove(partModel);
                break;
            case BUTTON:
                buttons.remove(partModel);
                break;
            default:
                throw new IllegalArgumentException("Bug! Can't delete this kind of part from a card: " + partModel.getType());
        }
    }

    /**
     * Adds a part (button or field) to this card.
     *
     * @param partModel The part to add.
     * @throws PartException Thrown if part type is unsupported.
     */
    public void addPartModel(PartModel partModel) throws PartException {
        switch (partModel.getType()) {
            case FIELD:
                fields.add((FieldModel)partModel);
                break;
            case BUTTON:
                buttons.add((ButtonModel)partModel);
                break;
            default:
                throw new IllegalArgumentException("Bug! Can't add this kind of part to a card: " + partModel.getType());
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
