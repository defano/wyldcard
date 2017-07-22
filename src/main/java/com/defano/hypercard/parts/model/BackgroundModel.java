/*
 * BackgroundModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypercard.Serializer;

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
public class BackgroundModel {

    private String name = "";
    private boolean cantDelete = false;
    private byte[] backgroundImage;
    private byte[] partsScreenshot;
    private Collection<ButtonModel> buttonModels;
    private Collection<FieldModel> fieldModels;

    private BackgroundModel() {
        buttonModels = new ArrayList<>();
        fieldModels = new ArrayList<>();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCantDelete() {
        return cantDelete;
    }

    public void setCantDelete(boolean cantDelete) {
        this.cantDelete = cantDelete;
    }

    public void setPartsScreenshot(BufferedImage image) {
        this.partsScreenshot = Serializer.serializeImage(image);
    }

    public BufferedImage getPartsScreenshot() {
        return Serializer.deserializeImage(this.partsScreenshot);
    }
}
