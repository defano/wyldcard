/*
 * BackgroundModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypercard.Serializer;
import com.defano.hypertalk.ast.common.Value;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A data model representing a card background. There is no view associated with this model; rather this data is
 * incorporated into the {@link com.defano.hypercard.parts.CardPart} view object when rendered.
 */
public class BackgroundModel extends PropertiesModel {

    public final static String PROP_NAME = "name";
    public final static String PROP_CANTDELETE = "cantdelete";

    private byte[] backgroundImage;
    private Collection<ButtonModel> buttonModels;
    private Collection<FieldModel> fieldModels;

    private BackgroundModel() {
        buttonModels = new ArrayList<>();
        fieldModels = new ArrayList<>();

        defineProperty(PROP_NAME, new Value(""), false);
        defineProperty(PROP_CANTDELETE, new Value(false), false);
    }

    public static BackgroundModel emptyBackground() {
        return new BackgroundModel();
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
