package com.defano.wyldcard.parts.builder;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.model.PartModel;

public class ButtonModelBuilder extends PartModelBuilder<ButtonModel, ButtonModelBuilder> {

    private final ButtonModel model;

    public ButtonModelBuilder(Owner owner, PartModel parentPartModel) {
        this.model = new ButtonModel(owner, parentPartModel);
    }

    public ButtonModelBuilder withIconId(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_ICON, new Value(v));
        return this;
    }

    public ButtonModelBuilder withStyle(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_STYLE, new Value(v));
        return this;
    }

    public ButtonModelBuilder withTextStyle(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_TEXTSTYLE, new Value(v));
        return this;
    }

    public ButtonModelBuilder withTextAlign(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_TEXTALIGN, new Value(v));
        return this;
    }

    public ButtonModelBuilder withTextFont(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_TEXTFONT, new Value(v));
        return this;
    }

    public ButtonModelBuilder withTextSize(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_TEXTSIZE, new Value(v));
        return this;
    }

    public ButtonModelBuilder withShowName(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_SHOWNAME, new Value(v));
        return this;
    }

    public ButtonModelBuilder withHilite(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_HIGHLIGHT, new Value(v));
        return this;
    }

    public ButtonModelBuilder withAutoHilite(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_AUTOHILIGHT, new Value(v));
        return this;
    }

    public ButtonModelBuilder withIsEnabled(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_ENABLED, new Value(v));
        return this;
    }

    public ButtonModelBuilder withFamily(Object v) {
        this.model.setKnownProperty(context, ButtonModel.PROP_FAMILY, new Value(v));
        return this;
    }

    @Override
    public ButtonModel build() {
        return model;
    }

    @Override
    public ButtonModelBuilder getBuilder() {
        return this;
    }
}
