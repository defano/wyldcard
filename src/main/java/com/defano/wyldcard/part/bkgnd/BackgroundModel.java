package com.defano.wyldcard.part.bkgnd;

import com.defano.hypertalk.ast.model.enums.LengthAdjective;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.part.button.ButtonModel;
import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.card.CardPart;
import com.defano.wyldcard.part.card.CardLayer;
import com.defano.wyldcard.part.field.FieldModel;
import com.defano.wyldcard.part.finder.LayeredPartFinder;
import com.defano.wyldcard.part.finder.OrderedPartFinder;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.part.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.serializer.BufferedImageSerializer;
import com.defano.hypertalk.ast.model.enums.Owner;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.defano.hypertalk.ast.model.Value;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A data model representing a card background. There is no view associated with this model; rather this data is
 * incorporated/merged into the {@link CardPart} controller object when rendered.
 */
public class BackgroundModel extends PartModel implements LayeredPartFinder, CardLayer {

    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_CANTDELETE = "cantdelete";
    public static final String PROP_DONTSEARCH = "dontsearch";
    public static final String PROP_SHOWPICT = "showpict";
    public static final String PROP_SHORTNAME = "short name";
    public static final String PROP_ABBREVNAME = "abbreviated name";
    public static final String PROP_LONGNAME = "long name";

    private BufferedImage backgroundImage;
    private final Collection<ButtonModel> buttonModels = new ArrayList<>();
    private final Collection<FieldModel> fieldModels = new ArrayList<>();

    public BackgroundModel(StackModel model) {
        super(PartType.BACKGROUND, Owner.STACK, model);

        define(PROP_ID).asConstant(new Value());
        define(PROP_NAME).asValue();
        define(PROP_CANTDELETE).asValue(false);
        define(PROP_DONTSEARCH).asValue(false);
        define(PROP_CONTENTS).asValue();
        define(PROP_SHOWPICT).asValue(true);

        postConstructBackgroundModel();
    }

    @PostConstruct
    public void postConstructBackgroundModel() {
        super.postConstructPartModel();

        findProperty(PROP_NAME).value().applyOnGetTransform((context, model, raw) -> {
            if (raw == null || raw.isEmpty()) {
                return new Value("bkgnd id " + model.get(context, PROP_ID));
            } else {
                return raw;
            }
        });

        define(PROP_NUMBER).asComputedReadOnlyValue((context, model) -> new Value(((OrderedPartFinder) ((BackgroundModel) model).getParentPartModel()).getPartNumber(context, (BackgroundModel) model, PartType.CARD)));
        define(PROP_LONGNAME).asComputedReadOnlyValue((context, model) -> new Value(getLongName(context)));
        define(PROP_ABBREVNAME).asComputedReadOnlyValue((context, model) -> new Value(getAbbrevName(context)));
        define(PROP_SHORTNAME).asComputedReadOnlyValue((context, model) -> new Value(getShortName(context)));
    }

    @Override
    public Collection<PartModel> getPartModels(ExecutionContext context) {
        Collection<PartModel> models = new ArrayList<>();

        models.addAll(buttonModels);
        models.addAll(fieldModels);
        models.addAll(getCardModels());

        return models;
    }

    /** {@inheritDoc} */
    @Override
    public LengthAdjective getDefaultAdjectiveForProperty(String propertyName) {
        if (propertyName.equalsIgnoreCase(PROP_NAME)) {
            return LengthAdjective.ABBREVIATED;
        } else {
            return LengthAdjective.DEFAULT;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAdjectiveSupportedProperty(String propertyName) {
        return propertyName.equalsIgnoreCase(PROP_NAME);
    }

    public StackModel getStackModel() {
        return (StackModel) getParentPartModel();
    }

    public Collection<FieldModel> getFieldModels() {
        return fieldModels;
    }

    public Collection<ButtonModel> getButtonModels() {
        return buttonModels;
    }

    public FieldModel getField(int id) {
        return getFieldModels().stream().filter(fm -> fm.getId() == id).findFirst().orElse(null);
    }

    public ButtonModel getButton(int id) {
        return getButtonModels().stream().filter(bm -> bm.getId() == id).findFirst().orElse(null);
    }

    public List<CardModel> getCardModels() {
        return ((StackModel) getParentPartModel()).getCardsInBackground(getId());
    }

    @Override
    public void addPartModel(PartModel model) {
        if (model instanceof FieldModel) {
            addFieldModel((FieldModel) model);
        } else if (model instanceof ButtonModel) {
            addButtonModel((ButtonModel) model);
        } else {
            throw new IllegalStateException("Bug! This part cannot be added to the background: " + model);
        }
    }

    private void addFieldModel(FieldModel model) {
        model.setParentPartModel(this);
        this.fieldModels.add(model);
    }

    private void addButtonModel(ButtonModel model) {
        model.setParentPartModel(this);
        this.buttonModels.add(model);
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public void removePartModel(ExecutionContext context, PartModel model) {
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

    public BufferedImage getBackgroundImage(Dimension dimension) {
        if (this.backgroundImage == null) {
            return BufferedImageSerializer.emptyImage(dimension);
        } else {
            return this.backgroundImage;
        }
    }

    public boolean hasBackgroundImage() {
        return this.backgroundImage != null;
    }

    public boolean hasName() {
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

    public String getAbbrevName(ExecutionContext context) {
        if (hasName()) {
            return "bkgnd \"" + getShortName(context) + "\"";
        } else {
            return getShortName(context);
        }
    }

    @Override
    public String getLongName(ExecutionContext context) {
        return getAbbrevName(context) + " of " + getStackModel().getLongName(context);
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
