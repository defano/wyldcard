package com.defano.hypercard.parts.card;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.bkgnd.BackgroundModel;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypercard.parts.finder.LayeredPartFinder;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.runtime.serializer.BufferedImageSerializer;
import com.defano.hypercard.runtime.serializer.Serializer;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypertalk.ast.model.Adjective;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.google.common.collect.Lists;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A data model representing a card in a stack. See {@link CardPart} for the associated
 * controller object.
 */
public class CardModel extends PartModel implements LayeredPartFinder {

    public final static String PROP_ID = "id";
    public final static String PROP_MARKED = "marked";
    public final static String PROP_CANTDELETE = "cantdelete";
    public final static String PROP_NAME = "name";
    public final static String PROP_SHOWPICT = "showpict";
    public static final String PROP_SHORTNAME = "short name";
    public static final String PROP_ABBREVNAME = "abbreviated name";
    public static final String PROP_LONGNAME = "long name";
    public static final String PROP_SHORTID = "short id";
    public static final String PROP_ABBREVID = "abbreviated id";
    public static final String PROP_LONGID = "long id";

    private int backgroundId = 0;
    private final Collection<FieldModel> fields = new ArrayList<>();
    private final Collection<ButtonModel> buttons = new ArrayList<>();
    private BufferedImage cardImage;

    private transient CardModelObserver observer;

    private CardModel(int cardId, int backgroundId, PartModel parentPartModel) {
        super(PartType.CARD, Owner.STACK, parentPartModel);

        this.backgroundId = backgroundId;

        defineProperty(PROP_ID, new Value(cardId), true);
        defineProperty(PROP_MARKED, new Value(false), false);
        defineProperty(PROP_CANTDELETE, new Value(false), false);
        defineProperty(PROP_NAME, new Value(""), false);
        defineProperty(PROP_CONTENTS, new Value(""), false);
        defineProperty(PROP_SHOWPICT, new Value(true), false);

        initialize();
    }

    @Override
    @PostConstruct
    public void initialize() {
        super.initialize();

        defineComputedReadOnlyProperty(PROP_LONGNAME, (model, propertyName) -> new Value(getLongName()));
        defineComputedReadOnlyProperty(PROP_ABBREVNAME, (model, propertyName) -> new Value(getAbbrevName()));
        defineComputedReadOnlyProperty(PROP_SHORTNAME, (model, propertyName) -> new Value(getShortName()));

        defineComputedReadOnlyProperty(PROP_LONGID, (model, propertyName) -> new Value(getLongId()));
        defineComputedReadOnlyProperty(PROP_ABBREVID, (model, propertyName) -> new Value(getAbbrevId()));
        defineComputedReadOnlyProperty(PROP_SHORTID, (model, propertyName) -> new Value(getShortId()));

        // When no name of card is provided, returns 'card id xxx'
        defineComputedGetterProperty(PROP_NAME, (model, propertyName) -> {
            Value raw = model.getRawProperty(propertyName);
            if (raw == null || raw.isEmpty()) {
                return new Value("card id " + model.getKnownProperty(PROP_ID));
            } else {
                return raw;
            }
        });

        // Card inherits size and location properties from the stack
        delegateProperties(Lists.newArrayList(PROP_RECT, PROP_RECTANGLE, PROP_TOP, PROP_LEFT, PROP_BOTTOM, PROP_RIGHT, PROP_TOPLEFT, PROP_BOTTOMRIGHT, PROP_HEIGHT, PROP_WIDTH, StackModel.PROP_RESIZABLE),
                property -> HyperCard.getInstance().getActiveStack().getStackModel());
    }

    /**
     * Returns a new CardModel containing no parts and and empty foreground canvas but which inherits the
     * specified background.
     *
     * @param backgroundId The ID of the background this card should inherit.
     * @return The new CardModel
     */
    public static CardModel emptyCardModel(int cardId, int backgroundId, PartModel parentPartModel) {
        return new CardModel(cardId, backgroundId, parentPartModel);
    }

    public Collection<FieldModel> getFieldModels() {
        return fields;
    }

    /**
     * Removes the specified part (button or field). Has no effect if the part doesn't exist on this card.
     *
     * @param partModel The part to remove from this card.
     */
    public void removePartModel(PartModel partModel) {
        if (partModel instanceof FieldModel) {
            if (partModel.getOwner() == Owner.BACKGROUND)  {
                getBackgroundModel().removePartModel(partModel);
            } else {
                fields.remove(partModel);
            }
        } else if (partModel instanceof ButtonModel) {
            if (partModel.getOwner() == Owner.BACKGROUND) {
                getBackgroundModel().removePartModel(partModel);
            } else {
                buttons.remove(partModel);
            }
        } else {
            throw new IllegalArgumentException("Bug! Can't delete this kind of part from a card: " + partModel.getType());
        }

        firePartRemoved(partModel);
    }

    /**
     * Adds a part (button or field) to this card.
     *
     * @param partModel The part to add.
     */
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
    public BufferedImage getCardImage() {
        if (cardImage == null) {
            return BufferedImageSerializer.emptyImage();
        } else {
            return this.cardImage;
        }
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
        this.setParentPartModel(parentPartModel);

        for (FieldModel thisField : fields) {
            thisField.relinkParentPartModel(this);
        }

        for (ButtonModel thisButton : buttons) {
            thisButton.relinkParentPartModel(this);
        }
    }

    public boolean isMarked() {
        return getKnownProperty(PROP_MARKED).booleanValue();
    }

    /** {@inheritDoc} */
    @Override
    public Adjective getDefaultAdjectiveForProperty(String propertyName) {
        if (propertyName.equalsIgnoreCase(PROP_NAME)) {
            return Adjective.ABBREVIATED;
        } else if (propertyName.equalsIgnoreCase(PROP_ID)) {
            return Adjective.ABBREVIATED;
        } else {
            return Adjective.DEFAULT;
        }
    }

    /** {@inheritDoc} */
    public boolean isAdjectiveSupportedProperty(String propertyName) {
        return propertyName.equalsIgnoreCase(PROP_NAME) || propertyName.equalsIgnoreCase(PROP_ID);
    }

    @Override
    public Collection<PartModel> getPartModels() {
        Collection<PartModel> models = new ArrayList<>();
        models.addAll(buttons);
        models.addAll(fields);
        return models;
    }

    /**
     * Gets the zero-based location of this card in its stack.
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

    public String getShortId() {
        return String.valueOf(getId());
    }

    public String getAbbrevId() {
        return "card id " + getShortId();
    }

    public String getLongId() {
        // TODO: Add "of stack..." portion once implemented in HyperTalk
        return getAbbrevId();
    }

    public boolean hasName() {
        Value raw = getRawProperty(PROP_NAME);
        return raw != null && !raw.isEmpty();
    }

    public String getShortName() {
        return getKnownProperty(PROP_NAME).stringValue();
    }

    public String getAbbrevName() {
        if (hasName()) {
            return "card \"" + getShortName() + "\"";
        } else {
            return getShortName();
        }
    }

    public String getLongName() {
        // TODO: Add "of stack..." portion once implemented in HyperTalk
        return getAbbrevName();
    }

    private void firePartRemoved(PartModel part) {
        if (observer != null) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> observer.onPartRemoved(part));
        }
    }

}