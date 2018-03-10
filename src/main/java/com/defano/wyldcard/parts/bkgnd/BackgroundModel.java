package com.defano.wyldcard.parts.bkgnd;

import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.parts.stack.StackModel;
import com.defano.wyldcard.runtime.serializer.BufferedImageSerializer;
import com.defano.hypertalk.ast.model.Adjective;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;

import javax.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A data model representing a card background. There is no view associated with this model; rather this data is
 * incorporated/merged into the {@link CardPart} controller object when rendered.
 */
public class BackgroundModel extends PartModel implements LayeredPartFinder {

    public final static String PROP_ID = "id";
    public final static String PROP_NAME = "name";
    public final static String PROP_CANTDELETE = "cantdelete";
    public final static String PROP_SHOWPICT = "showpict";

    public static final String PROP_SHORTNAME = "short name";
    public static final String PROP_ABBREVNAME = "abbreviated name";
    public static final String PROP_LONGNAME = "long name";

    private BufferedImage backgroundImage;
    private final Collection<ButtonModel> buttonModels;
    private final Collection<FieldModel> fieldModels;

    private BackgroundModel(int backgroundId, PartModel parentPartModel) {
        super(PartType.BACKGROUND, Owner.STACK, parentPartModel);

        buttonModels = new ArrayList<>();
        fieldModels = new ArrayList<>();

        defineProperty(PROP_ID, new Value(backgroundId), true);
        defineProperty(PROP_NAME, new Value(""), false);
        defineProperty(PROP_CANTDELETE, new Value(false), false);
        defineProperty(PROP_CONTENTS, new Value(""), false);
        defineProperty(PROP_SHOWPICT, new Value(true), false);

        initialize();
    }

    public static BackgroundModel emptyBackground(int backgroundId, PartModel parentPartModel) {
        return new BackgroundModel(backgroundId, parentPartModel);
    }

    @Override
    @PostConstruct
    public void initialize() {
        super.initialize();

        // When no name of card is provided, returns 'background id xxx'
        defineComputedGetterProperty(PROP_NAME, (model, propertyName) -> {
            Value raw = model.getRawProperty(propertyName);
            if (raw == null || raw.isEmpty()) {
                return new Value("bkgnd id " + model.getKnownProperty(PROP_ID));
            } else {
                return raw;
            }
        });

        defineComputedReadOnlyProperty(PROP_LONGNAME, (model, propertyName) -> new Value(getLongName()));
        defineComputedReadOnlyProperty(PROP_ABBREVNAME, (model, propertyName) -> new Value(getAbbrevName()));
        defineComputedReadOnlyProperty(PROP_SHORTNAME, (model, propertyName) -> new Value(getShortName()));
    }

    @Override
    public Collection<PartModel> getPartModels() {
        Collection<PartModel> models = new ArrayList<>();

        models.addAll(buttonModels);
        models.addAll(fieldModels);
        models.addAll(getCardModels());

        return models;
    }

    /** {@inheritDoc} */
    @Override
    public Adjective getDefaultAdjectiveForProperty(String propertyName) {
        if (propertyName.equalsIgnoreCase(PROP_NAME)) {
            return Adjective.ABBREVIATED;
        } else {
            return Adjective.DEFAULT;
        }
    }

    /** {@inheritDoc} */
    public boolean isAdjectiveSupportedProperty(String propertyName) {
        return propertyName.equalsIgnoreCase(PROP_NAME);
    }

    public StackModel getStackModel() {
        return (StackModel) getParentPartModel();
    }

    public Collection<FieldModel> getFieldModels() {
        return fieldModels;
    }

    public List<CardModel> getCardModels() {
        return ((StackModel) getParentPartModel()).getCardsInBackground(getId());
    }

    public void addFieldModel(FieldModel model) {
        model.setParentPartModel(this);
        this.fieldModels.add(model);
    }

    public void addButtonModel(ButtonModel model) {
        model.setParentPartModel(this);
        this.buttonModels.add(model);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void removePartModel(PartModel model) {
        switch (model.getType()) {
            case FIELD:
                fieldModels.remove(model);
                break;
            case BUTTON:
                buttonModels.remove(model);
                break;
            default:
                throw new IllegalArgumentException("Bug! Can't delete this kind of part from a background: " + model.getType());
        }
    }

    public void setBackgroundImage(BufferedImage image) {
        this.backgroundImage = image;
    }

    public BufferedImage getBackgroundImage() {
        if (this.backgroundImage == null) {
            return BufferedImageSerializer.emptyImage();
        } else {
            return this.backgroundImage;
        }
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
            return "bkgnd \"" + getShortName() + "\"";
        } else {
            return getShortName();
        }
    }

    public String getLongName() {
        // TODO: Add "of stack..." portion once implemented in HyperTalk
        return getAbbrevName();
    }

    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        setParentPartModel(parentPartModel);

        for (ButtonModel thisButton : buttonModels) {
            thisButton.relinkParentPartModel(this);
        }

        for (FieldModel thisField : fieldModels) {
            thisField.relinkParentPartModel(this);
        }
    }

}
