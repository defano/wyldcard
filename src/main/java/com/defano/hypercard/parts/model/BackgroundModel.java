/*
 * BackgroundModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

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

    private byte[] backgroundImage;
    private Collection<ButtonModel> buttonModels;
    private Collection<FieldModel> fieldModels;

    private BackgroundModel() {
        buttonModels = new ArrayList<>();
        fieldModels = new ArrayList<>();
    }

    public static BackgroundModel emptyBackground() {
        return new BackgroundModel();
    }

    public void setBackgroundImage(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            this.backgroundImage = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to save the card image.", e);
        }
    }

    public BufferedImage getBackgroundImage() {
        if (backgroundImage == null || backgroundImage.length == 0) {
            return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        } else {
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(backgroundImage);
                return ImageIO.read(stream);
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while reading the card image. The stack may be corrupted.", e);
            }
        }
    }

}
