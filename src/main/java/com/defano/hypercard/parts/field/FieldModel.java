/*
 * FieldModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.field;

import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;

import java.awt.*;

/**
 * A data model representing a field part on a card. See {@link FieldPart} for the associated
 * view object.
 */
public class FieldModel extends CardLayerPartModel {

    public static final String PROP_TEXT = "text";
    public static final String PROP_DONTWRAP = "dontwrap";
    public static final String PROP_LOCKTEXT = "locktext";
    public static final String PROP_SHOWLINES = "showlines";
    public static final String PROP_STYLE = "style";

    private byte[] styleData;

    public FieldModel (Owner owner) {
        super(PartType.FIELD, owner);
    }

    public static FieldModel newFieldModel(int id, Rectangle geometry, Owner owner) {
        FieldModel partModel = new FieldModel(owner);

        partModel.defineProperty(PROP_SCRIPT, new Value(), false);
        partModel.defineProperty(PROP_ID, new Value(id), true);
        partModel.defineProperty(PROP_NAME, new Value("Text Field " + id), false);
        partModel.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        partModel.defineProperty(PROP_TOP, new Value(geometry.y), false);
        partModel.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        partModel.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        partModel.defineProperty(PROP_DONTWRAP, new Value(false), false);
        partModel.defineProperty(PROP_VISIBLE, new Value(true), false);
        partModel.defineProperty(PROP_LOCKTEXT, new Value(false), false);
        partModel.defineProperty(PROP_SHOWLINES, new Value(true), false);
        partModel.defineProperty(PROP_STYLE, new Value(FieldStyle.RECTANGLE.getName()), false);
        partModel.defineProperty(PROP_TEXT, new Value(""), false);
        partModel.defineProperty(PROP_TEXTALIGN, new Value("left"), false);
        partModel.defineProperty(PROP_CONTENTS, new Value(""), false);

        return partModel;
    }

    public byte[] getStyleData() {
        return styleData;
    }

    public void setStyleData(byte[] styleData) {
        this.styleData = styleData;
    }

    /** {@inheritDoc} */
    @Override
    public String getValueProperty() {
        return PROP_TEXT;
    }
}