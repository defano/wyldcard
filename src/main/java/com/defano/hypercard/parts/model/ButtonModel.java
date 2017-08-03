/*
 * ButtonModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypercard.parts.buttons.ButtonStyle;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;

import java.awt.*;

/**
 * A data model representing a button part on a card. See {@link com.defano.hypercard.parts.ButtonPart} for the
 * associated view object.
 */
public class ButtonModel extends CardLayerPartModel {

    public static final String PROP_STYLE = "style";
    public static final String PROP_FAMILY = "family";
    public static final String PROP_HILITE = "hilite";
    public static final String PROP_AUTOHILIGHT = "autohilite";
    public static final String PROP_SHOWNAME = "showname";

    private ButtonModel(Owner owner) {
        super(PartType.BUTTON, owner);
    }

    public static ButtonModel newButtonModel(Integer id, Rectangle geometry, Owner owner) {
        ButtonModel partModel = new ButtonModel(owner);

        partModel.defineProperty(PROP_SCRIPT, new Value(), false);
        partModel.defineProperty(PROP_ID, new Value(id), true);
        partModel.defineProperty(PROP_NAME, new Value("Button"), false);
        partModel.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        partModel.defineProperty(PROP_TOP, new Value(geometry.y), false);
        partModel.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        partModel.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        partModel.defineProperty(PROP_SHOWNAME, new Value(true), false);
        partModel.defineProperty(PROP_STYLE, new Value(ButtonStyle.DEFAULT.getName()), false);
        partModel.defineProperty(PROP_FAMILY, new Value(), false);
        partModel.defineProperty(PROP_HILITE, new Value(false), false);
        partModel.defineProperty(PROP_AUTOHILIGHT, new Value(true), false);

        return partModel;
    }

}