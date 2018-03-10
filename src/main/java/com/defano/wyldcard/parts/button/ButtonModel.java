package com.defano.wyldcard.parts.button;

import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.List;

/**
 * A data model representing a button. See {@link ButtonPart} for the associated controller object.
 */
public class ButtonModel extends CardLayerPartModel {

    public static final String PROP_STYLE = "style";
    public static final String PROP_FAMILY = "family";
    public static final String PROP_HILITE = "hilite";
    public static final String PROP_HIGHLITE = "highlite";
    public static final String PROP_HILIGHT = "hilight";
    public static final String PROP_HIGHLIGHT = "highlight";
    public static final String PROP_AUTOHILITE = "autohilite";
    public static final String PROP_AUTOHIGHLITE = "autohighlite";
    public static final String PROP_AUTOHILIGHT = "autohilight";
    public static final String PROP_AUTOHIGHLIGHT = "autohighlight";

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

        partModel.setCurrentCardId(parentPartModel.getId());

        partModel.defineProperty(PROP_SCRIPT, new Value(), false);
        partModel.defineProperty(PROP_ID, new Value(id), true);
        partModel.defineProperty(PROP_NAME, new Value("New Button"), false);
        partModel.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        partModel.defineProperty(PROP_TOP, new Value(geometry.y), false);
        partModel.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        partModel.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        partModel.defineProperty(PROP_SHOWNAME, new Value(true), false);
        partModel.defineProperty(PROP_STYLE, new Value(ButtonStyle.ROUND_RECT.getName()), false);
        partModel.defineProperty(PROP_FAMILY, new Value(), false);
        partModel.defineProperty(PROP_HILITE, new Value(false), false);
        partModel.defineProperty(PROP_AUTOHILITE, new Value(true), false);
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

        definePropertyAlias(PROP_HILITE, PROP_HIGHLITE, PROP_HILIGHT, PROP_HIGHLIGHT);
        definePropertyAlias(PROP_AUTOHILITE, PROP_AUTOHIGHLITE, PROP_AUTOHILIGHT, PROP_AUTOHIGHLIGHT);
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