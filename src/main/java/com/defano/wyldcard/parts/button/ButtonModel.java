package com.defano.wyldcard.parts.button;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A data model representing a button. See {@link ButtonPart} for the associated controller object.
 */
public class ButtonModel extends CardLayerPartModel implements PropertyChangeObserver {

    public static final String PROP_FAMILY = "family";
    public static final String ALIAS_HILITE = "hilite";
    public static final String ALIAS_HIGHLITE = "highlite";
    public static final String ALIAS_HILIGHT = "hilight";
    public static final String PROP_SHAREDHILITE = "sharedhilite";
    public static final String PROP_HIGHLIGHT = "highlight";
    public static final String ALIAS_AUTOHILITE = "autohilite";
    public static final String ALIAS_AUTOHIGHLITE = "autohighlite";
    public static final String ALIAS_AUTOHILIGHT = "autohilight";
    public static final String PROP_AUTOHIGHLIGHT = "autohighlight";
    public static final String PROP_SHOWNAME = "showname";
    public static final String PROP_ICON = "icon";
    public static final String PROP_ICONALIGN = "iconalign";

    // "Hidden" internal property not addressable in HyperTalk; represents combo-box selection
    public static final String PROP_SELECTEDITEM = "--selectedindex--";

    private final Map<Integer, Boolean> unsharedHilite = new HashMap<>();
    private boolean sharedHiliteState = false;

    public ButtonModel(Owner owner, PartModel parentPartModel) {
        super(PartType.BUTTON, owner, parentPartModel);

        this.setCurrentCardId(parentPartModel.getId());

        define(PROP_SCRIPT).asValue();
        define(PROP_ID).asConstant(new Value());
        define(PROP_NAME).asValue("New Button");
        define(PROP_LEFT).asValue();
        define(PROP_TOP).asValue();
        define(PROP_WIDTH).asValue();
        define(PROP_HEIGHT).asValue();
        define(PROP_SHOWNAME).asValue(true);
        define(PROP_STYLE).asValue(ButtonStyle.ROUND_RECT.toString());
        define(PROP_FAMILY).asValue();
        define(PROP_AUTOHIGHLIGHT).asValue(true);
        define(PROP_CONTENTS).asValue();
        define(PROP_ICON).asValue();
        define(PROP_ICONALIGN).asValue("default");
        define(PROP_SELECTEDITEM).asValue();
        define(PROP_SHAREDHILITE).asValue(true);

        postConstructButtonModel();
    }

    @PostConstruct
    private void postConstructButtonModel() {
        super.postConstructCardLayerPartModel();

        define(PROP_NUMBER).asComputedReadOnlyValue((context, model) -> new Value(((LayeredPartFinder) ((ButtonModel) model).getParentPartModel()).getPartNumber(context, (ButtonModel) model, PartType.BUTTON)));
        define(PROP_SELECTEDLINE).asComputedReadOnlyValue((context, model) -> new Value(getSelectedLineExpression(context)));
        define(PROP_SELECTEDTEXT).asComputedReadOnlyValue((context, model) -> {
            List<Value> lines = get(context, PROP_CONTENTS).getLines(context);
            int selectedLineIdx = get(context, PROP_SELECTEDITEM).integerValue() - 1;

            // Invalid state... shouldn't be possible
            if (selectedLineIdx < 0 || selectedLineIdx >= lines.size()) {
                return new Value();
            }

            return lines.get(selectedLineIdx);
        });

        define(ALIAS_AUTOHIGHLITE, ALIAS_AUTOHILIGHT, ALIAS_AUTOHILITE).asAliasOf(PROP_AUTOHIGHLIGHT);

        define(PROP_HIGHLIGHT, ALIAS_HILITE, ALIAS_HIGHLITE, ALIAS_HILIGHT).asComputedValue()
                .withGetter((context, model) -> getHilite(context))
                .withSetter((context, model, value) -> setHilite(context, value));

        // When an icon has been applied to a button, HyperCard automatically forces the button font to 10pt Geneva
        addPropertyChangedObserver(this);
    }

    private void setHilite(ExecutionContext context, Value hilite) {
        setHilite(context, getCurrentCardId(context), hilite);
    }

    private void setHilite(ExecutionContext context, int forCardId, Value hilite) {
        if (isSharedHilite(context)) {
            sharedHiliteState = hilite.booleanValue();
        } else {
            unsharedHilite.put(forCardId, hilite.booleanValue());
        }
    }

    private Value getHilite(ExecutionContext context) {
        return getHilite(context, getCurrentCardId(context));
    }

    private Value getHilite(ExecutionContext context, int forCardId) {
        if (isSharedHilite(context)) {
            return new Value(sharedHiliteState);
        } else {
            return new Value(unsharedHilite.getOrDefault(forCardId, false));
        }
    }

    private boolean isSharedHilite(ExecutionContext context) {
        return getOwner() == Owner.CARD || get(context, ButtonModel.PROP_SHAREDHILITE).booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        setParentPartModel(parentPartModel);
    }

    private String getSelectedLineExpression(ExecutionContext context) {
        Value selectedItem = get(context, PROP_SELECTEDITEM);
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

    @Override
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        if (property.equalsIgnoreCase(PROP_ICON) && !newValue.isZero()) {
            set(context, PROP_TEXTSIZE, new Value(10));
            set(context, PROP_TEXTFONT, new Value("Geneva"));
        }
    }
}