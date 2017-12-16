package com.defano.hypercard.parts.button;

import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.finder.LayeredPartFinder;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.List;

/**
 * A data model representing a button part on a card. See {@link ButtonPart} for the
 * associated view object.
 */
public class ButtonModel extends CardLayerPartModel {

    public static final String PROP_STYLE = "style";
    public static final String PROP_FAMILY = "family";
    public static final String PROP_HILITE = "hilite";
    public static final String PROP_AUTOHILIGHT = "autohilite";
    public static final String PROP_SHOWNAME = "showname";
    public static final String PROP_ICON = "icon";
    public static final String PROP_ICONALIGN = "iconalign";

    // "Hidden" internal property not addressable in HyperTalk; represents combo-box selection
    public static final String PROP_SELECTEDITEM = "--selectedindex--";

    private ButtonModel(Owner owner, PartModel parentPartModel) {
        super(PartType.BUTTON, owner, parentPartModel);
        initialize();
    }

    public static ButtonModel newButtonModel(Integer id, Rectangle geometry, Owner owner, PartModel parentPartModel) {
        ButtonModel partModel = new ButtonModel(owner, parentPartModel);

        partModel.defineProperty(PROP_SCRIPT, new Value(), false);
        partModel.defineProperty(PROP_ID, new Value(id), true);
        partModel.defineProperty(PROP_NAME, new Value("New Button"), false);
        partModel.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        partModel.defineProperty(PROP_TOP, new Value(geometry.y), false);
        partModel.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        partModel.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        partModel.defineProperty(PROP_SHOWNAME, new Value(true), false);
        partModel.defineProperty(PROP_STYLE, new Value(ButtonStyle.DEFAULT.getName()), false);
        partModel.defineProperty(PROP_FAMILY, new Value(), false);
        partModel.defineProperty(PROP_HILITE, new Value(false), false);
        partModel.defineProperty(PROP_AUTOHILIGHT, new Value(true), false);
        partModel.defineProperty(PROP_CONTENTS, new Value(), false);
        partModel.defineProperty(PROP_ICON, new Value(), false);
        partModel.defineProperty(PROP_ICONALIGN, new Value("default"), false);
        partModel.defineProperty(PROP_SELECTEDITEM, new Value(), false);

        return partModel;
    }

    @PostConstruct
    @Override
    public void initialize() {
        super.initialize();

        defineComputedReadOnlyProperty(PROP_SELECTEDLINE, (model, propertyName) -> new Value(getSelectedLineExpression()));
        defineComputedReadOnlyProperty(PROP_SELECTEDTEXT, (model, propertyName) -> {
            List<Value> lines = getKnownProperty(PROP_CONTENTS).getLines();
            int selectedLineIdx = getKnownProperty(PROP_SELECTEDITEM).integerValue() - 1;

            // Invalid state... shouldn't be possible
            if (selectedLineIdx < 0 || selectedLineIdx >= lines.size()) {
                return new Value();
            }

            return lines.get(selectedLineIdx);
        });
    }

    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        setParentPartModel(parentPartModel);
    }

    public long getButtonNumber() {
        return ((LayeredPartFinder) getParentPartModel()).getPartNumber(this, PartType.BUTTON);
    }

    public long getButtonCount() {
        return ((LayeredPartFinder) getParentPartModel()).getPartCount(PartType.BUTTON, getOwner());
    }

    private String getSelectedLineExpression() {
        Value selectedItem = getKnownProperty(PROP_SELECTEDITEM);
        if (selectedItem.isEmpty()) {
            return "";
        } else {
            return "line " +
                    (selectedItem.integerValue()) +
                    " of " +
                    getOwner().hyperTalkName.toLowerCase() +
                    " button id " +
                    getId();
        }
    }

}