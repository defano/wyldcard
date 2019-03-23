package com.defano.wyldcard.parts.button;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import javax.annotation.PostConstruct;
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

    public ButtonModel(Owner owner, PartModel parentPartModel) {
        super(PartType.BUTTON, owner, parentPartModel);

        setCurrentCardId(parentPartModel.getId(new ExecutionContext()));

        newProperty(PROP_SCRIPT, new Value(), false);
        newProperty(PROP_ID, new Value(), true);
        newProperty(PROP_NAME, new Value("New Button"), false);
        newProperty(PROP_LEFT, new Value(), false);
        newProperty(PROP_TOP, new Value(), false);
        newProperty(PROP_WIDTH, new Value(), false);
        newProperty(PROP_HEIGHT, new Value(), false);
        newProperty(PROP_SHOWNAME, new Value(true), false);
        newProperty(PROP_STYLE, new Value(ButtonStyle.ROUND_RECT.toString()), false);
        newProperty(PROP_FAMILY, new Value(), false);
        newProperty(PROP_HILITE, new Value(false), false);
        newProperty(PROP_AUTOHILITE, new Value(true), false);
        newProperty(PROP_CONTENTS, new Value(), false);
        newProperty(PROP_ICON, new Value(), false);
        newProperty(PROP_ICONALIGN, new Value("default"), false);
        newProperty(PROP_SELECTEDITEM, new Value(), false);
        
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @PostConstruct
    @Override
    public void initialize() {
        super.initialize();

        newComputedReadOnlyProperty(PROP_NUMBER, (context, model, propertyName) -> new Value(((LayeredPartFinder) ((ButtonModel) model).getParentPartModel()).getPartNumber(context, (ButtonModel) model, PartType.BUTTON)));
        newComputedReadOnlyProperty(PROP_SELECTEDLINE, (context, model, propertyName) -> new Value(getSelectedLineExpression(context)));
        newComputedReadOnlyProperty(PROP_SELECTEDTEXT, (context, model, propertyName) -> {
            List<Value> lines = getKnownProperty(context, PROP_CONTENTS).getLines(context);
            int selectedLineIdx = getKnownProperty(context, PROP_SELECTEDITEM).integerValue() - 1;

            // Invalid state... shouldn't be possible
            if (selectedLineIdx < 0 || selectedLineIdx >= lines.size()) {
                return new Value();
            }

            return lines.get(selectedLineIdx);
        });

        newPropertyAlias(PROP_HILITE, PROP_HIGHLITE, PROP_HILIGHT, PROP_HIGHLIGHT);
        newPropertyAlias(PROP_AUTOHILITE, PROP_AUTOHIGHLITE, PROP_AUTOHILIGHT, PROP_AUTOHIGHLIGHT);

        // When an icon has been applied to a button, HyperCard automatically forces the button font to 10pt Geneva
        addPropertyWillChangeObserver((context, property, oldValue, newValue) -> {
            if (property.equalsIgnoreCase(PROP_ICON) && !newValue.isEmpty()) {
                setKnownProperty(context, PROP_TEXTSIZE, new Value(10));
                setKnownProperty(context, PROP_TEXTFONT, new Value("Geneva"));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        setParentPartModel(parentPartModel);
    }

    private String getSelectedLineExpression(ExecutionContext context) {
        Value selectedItem = getKnownProperty(context, PROP_SELECTEDITEM);
        if (selectedItem.isEmpty()) {
            return "";
        } else {
            return "line " +
                    (selectedItem.integerValue()) +
                    " of " +
                    getOwner().hyperTalkName.toLowerCase() +
                    " button id " +
                    getId(context);
        }
    }

}