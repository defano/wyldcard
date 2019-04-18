package com.defano.wyldcard.parts.card;

import com.defano.hypertalk.ast.model.LengthAdjective;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.parts.NamedPart;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.finder.OrderedPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.serializer.BufferedImageSerializer;
import com.defano.wyldcard.runtime.serializer.Serializer;
import com.defano.wyldcard.thread.Invoke;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A data model representing a card in a stack. See {@link CardPart} for the associated
 * controller object.
 */
@SuppressWarnings("WeakerAccess")
public class CardModel extends PartModel implements LayeredPartFinder, NamedPart, PartOwner {

    public final static String PROP_ID = "id";
    public final static String PROP_MARKED = "marked";
    public final static String PROP_CANTDELETE = "cantdelete";
    public final static String PROP_DONTSEARCH = "dontsearch";
    public final static String PROP_NAME = "name";
    public final static String PROP_SHOWPICT = "showpict";
    public static final String PROP_SHORTNAME = "short name";
    public static final String PROP_ABBREVNAME = "abbreviated name";
    public static final String PROP_LONGNAME = "long name";
    public static final String PROP_SHORTID = "short id";
    public static final String PROP_ABBREVID = "abbreviated id";
    public static final String PROP_LONGID = "long id";
    public static final String PROP_OWNER = "owner";
    public static final String PROP_LONGOWNER = "long owner";
    public static final String PROP_SHORTOWNER = "short owner";

    private final Collection<FieldModel> fields = new ArrayList<>();
    private final Collection<ButtonModel> buttons = new ArrayList<>();
    private int backgroundId;
    private BufferedImage cardImage;

    private transient CardModelObserver observer;

    public CardModel(StackModel parentPartModel) {
        super(PartType.CARD, Owner.STACK, parentPartModel);

        define(PROP_ID).asConstant(new Value());
        define(PROP_MARKED).asValue(false);
        define(PROP_CANTDELETE).asValue(false);
        define(PROP_DONTSEARCH).asValue(false);
        define(PROP_NAME).asValue();
        define(PROP_CONTENTS).asValue();
        define(PROP_SHOWPICT).asValue(true);
        define(PROP_TOP).asConstant(0);
        define(PROP_LEFT).asConstant(0);
        define(PROP_TOPLEFT).asConstant("0,0");

        postConstructCardModel();
    }

    @PostConstruct
    public void postConstructCardModel() {
        super.postConstructAdvancedPropertiesModel();

        define(PROP_NUMBER).asComputedReadOnlyValue((context, model) -> new Value(((OrderedPartFinder) ((CardModel) model).getParentPartModel()).getPartNumber(context, (CardModel) model, PartType.CARD)));

        define(PROP_OWNER).asComputedReadOnlyValue((context, model) -> new Value(getBackgroundModel().getName(context)));
        define(PROP_LONGOWNER).asComputedReadOnlyValue((context, model) -> new Value(getBackgroundModel().getLongName(context)));
        define(PROP_SHORTOWNER).asComputedReadOnlyValue((context, model) -> new Value(getBackgroundModel().getShortName(context)));

        define(PROP_LONGNAME).asComputedReadOnlyValue((context, model) -> new Value(getLongName(context)));
        define(PROP_ABBREVNAME).asComputedReadOnlyValue((context, model) -> new Value(getAbbreviatedName(context)));
        define(PROP_SHORTNAME).asComputedReadOnlyValue((context, model) -> new Value(getShortName(context)));

        define(PROP_LONGID).asComputedReadOnlyValue((context, model) -> new Value(getLongId(context)));
        define(PROP_ABBREVID).asComputedReadOnlyValue((context, model) -> new Value(getAbbrevId(context)));
        define(PROP_SHORTID).asComputedReadOnlyValue((context, model) -> new Value(getShortId(context)));

        findProperty(PROP_NAME).value().applyOnGetTransform((context, model, raw) -> {
            if (raw == null || raw.isEmpty()) {
                return new Value("card id " + model.get(context, PROP_ID));
            } else {
                return raw;
            }
        });

        define(PROP_BOTTOM).asComputedReadOnlyValue((context, model) -> model.get(context, PROP_HEIGHT));
        define(PROP_RIGHT).asComputedReadOnlyValue((context, model) -> model.get(context, PROP_WIDTH));
        define(PROP_BOTTOMRIGHT).asComputedReadOnlyValue((context, model) -> new Value(new Point(
                get(context, PROP_WIDTH).integerValue(),
                get(context, PROP_HEIGHT).integerValue()
        )));
        define(PROP_RECT, PROP_RECTANGLE).asComputedReadOnlyValue((context, model) -> new Value(new Rectangle(
                0,
                0,
                get(context, PROP_WIDTH).integerValue(),
                get(context, PROP_HEIGHT).integerValue()
        )));

        define(PROP_HEIGHT, PROP_WIDTH, StackModel.PROP_RESIZABLE).byDelegatingToModel(context -> context.getCurrentStack().getStackModel());
    }

    @Override
    public Collection<FieldModel> getFieldModels() {
        return fields;
    }

    @Override
    public Collection<ButtonModel> getButtonModels() {
        return buttons;
    }

    @Override
    public void removePartModel(ExecutionContext context, PartModel partModel) {
        if (partModel instanceof FieldModel) {
            if (partModel.getOwner() == Owner.BACKGROUND) {
                getBackgroundModel().removePartModel(context, partModel);
            } else {
                fields.remove(partModel);
            }
        } else if (partModel instanceof ButtonModel) {
            if (partModel.getOwner() == Owner.BACKGROUND) {
                getBackgroundModel().removePartModel(context, partModel);
            } else {
                buttons.remove(partModel);
            }
        } else {
            throw new IllegalArgumentException("Bug! Can't delete this kind of part from a card: " + partModel.getType());
        }

        firePartRemoved(context, partModel);
    }

    @Override
    public void addPartModel(PartModel partModel) {
        if (partModel instanceof FieldModel) {
            fields.add((FieldModel) partModel);
        } else if (partModel instanceof ButtonModel) {
            buttons.add((ButtonModel) partModel);
        } else {
            throw new IllegalArgumentException("Bug! Can't add this kind of part to a card: " + partModel.getType());
        }

        partModel.setParentPartModel(this);
    }

    /**
     * Gets the background ID of this card.
     *
     * @return The ID of this card's background.
     */
    public int getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(int backgroundId) {
        this.backgroundId = backgroundId;
    }

    public BackgroundModel getBackgroundModel() {
        return ((StackModel) getParentPartModel()).getBackground(getBackgroundId());
    }

    public StackModel getStackModel() {
        return getBackgroundModel().getStackModel();
    }

    /**
     * Sets the image representing this card's foreground graphics.
     *
     * @param image The card image.
     */
    public void setCardImage(BufferedImage image) {
        this.cardImage = image;
    }

    /**
     * Returns the image of this card's foreground.
     *
     * @return The foreground image.
     */
    public BufferedImage getCardImage(Dimension dimension) {
        if (cardImage == null) {
            return BufferedImageSerializer.emptyImage(dimension);
        } else {
            return this.cardImage;
        }
    }

    public boolean hasCardImage() {
        return cardImage != null;
    }

    /**
     * Create's a deep copy of this card.
     *
     * @return A copy of this card.
     */
    public CardModel copyOf() {
        return Serializer.copy(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValueProperty() {
        return PROP_CONTENTS;
    }

    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        setParentPartModel(parentPartModel);

        for (FieldModel thisField : fields) {
            thisField.relinkParentPartModel(this);
        }

        for (ButtonModel thisButton : buttons) {
            thisButton.relinkParentPartModel(this);
        }
    }

    public boolean isMarked(ExecutionContext context) {
        return get(context, PROP_MARKED).booleanValue();
    }

    public void setMarked(ExecutionContext context, boolean marked) {
        set(context, PROP_MARKED, new Value(marked));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LengthAdjective getDefaultAdjectiveForProperty(String propertyName) {
        if (propertyName.equalsIgnoreCase(PROP_NAME)) {
            return LengthAdjective.ABBREVIATED;
        } else if (propertyName.equalsIgnoreCase(PROP_ID)) {
            return LengthAdjective.ABBREVIATED;
        } else {
            return LengthAdjective.DEFAULT;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAdjectiveSupportedProperty(String propertyName) {
        return propertyName.equalsIgnoreCase(PROP_NAME) || propertyName.equalsIgnoreCase(PROP_ID);
    }

    @Override
    public Collection<PartModel> getPartModels(ExecutionContext context) {
        Collection<PartModel> models = new ArrayList<>();
        models.addAll(buttons);
        models.addAll(fields);

        return models;
    }

    /**
     * Gets the zero-based location of this card in its stack.
     *
     * @return The location of this card in the stack.
     */
    public int getCardIndexInStack() {
        return ((StackModel) getParentPartModel()).getIndexOfCard(this);
    }

    public CardModelObserver getObserver() {
        return observer;
    }

    public void setObserver(CardModelObserver observer) {
        this.observer = observer;
    }

    public String getShortId(ExecutionContext context) {
        return String.valueOf(getId(context));
    }

    public String getAbbrevId(ExecutionContext context) {
        return "card id " + getShortId(context);
    }

    public String getLongId(ExecutionContext context) {
        return getAbbrevId(context) + " of " + getStackModel().getLongName(context);
    }

    private boolean hasName() {
        try {
            return !findProperty(PROP_NAME).value().get(new ExecutionContext(), null).isEmpty();
        } catch (HtException e) {
            return false;
        }
    }

    @Override
    public String getShortName(ExecutionContext context) {
        return get(context, PROP_NAME).toString();
    }

    @Override
    public String getAbbreviatedName(ExecutionContext context) {
        if (hasName()) {
            return "card \"" + getShortName(context) + "\"";
        } else {
            return getShortName(context);
        }
    }

    @Override
    public String getLongName(ExecutionContext context) {
        return getAbbreviatedName(context) + " of " + getStackModel().getLongName(context);
    }

    private void firePartRemoved(ExecutionContext context, PartModel part) {
        if (observer != null) {
            Invoke.onDispatch(() -> observer.onPartRemoved(context, part));
        }
    }

}