/*
 * BackgroundModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.bkgnd;

import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.serializer.Serializer;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A data model representing a card background. There is no view associated with this model; rather this data is
 * incorporated into the {@link CardPart} view object when rendered.
 */
public class BackgroundModel extends PartModel {

    public final static String PROP_ID = "id";
    public final static String PROP_NAME = "name";
    public final static String PROP_CANTDELETE = "cantdelete";

    private byte[] backgroundImage;
    private final Collection<ButtonModel> buttonModels;
    private final Collection<FieldModel> fieldModels;

    private BackgroundModel(int backgroundId) {
        super(PartType.BACKGROUND, Owner.STACK);

        buttonModels = new ArrayList<>();
        fieldModels = new ArrayList<>();

        defineProperty(PROP_ID, new Value(backgroundId), true);
        defineProperty(PROP_NAME, new Value(""), false);
        defineProperty(PROP_CANTDELETE, new Value(false), false);
        defineProperty(PROP_CONTENTS, new Value(""), false);

        // When no name of card is provided, returns 'background id xxx'
        defineComputedGetterProperty(PROP_NAME, (model, propertyName) -> {
            Value raw = model.getRawProperty(propertyName);
            if (raw == null || raw.isEmpty()) {
                return new Value("background id " + model.getKnownProperty(PROP_ID));
            } else {
                return raw;
            }
        });

    }

    public static BackgroundModel emptyBackground(int backgroundId) {
        return new BackgroundModel(backgroundId);
    }

    public Collection<PartModel> getPartModels() {
        Collection<PartModel> models = new ArrayList<>();
        models.addAll(buttonModels);
        models.addAll(fieldModels);

        return models;
    }

    public void addFieldModel(FieldModel model) {
        this.fieldModels.add(model);
    }

    public void addButtonModel(ButtonModel model) {
        this.buttonModels.add(model);
    }

    public void setBackgroundImage(BufferedImage image) {
        this.backgroundImage = Serializer.serializeImage(image);
    }

    public BufferedImage getBackgroundImage() {
        return Serializer.deserializeImage(this.backgroundImage);
    }
}
