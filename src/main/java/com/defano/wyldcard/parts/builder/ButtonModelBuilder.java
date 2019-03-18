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
        this.model.newProperty(ButtonModel.PROP_ICON, new Value(v), true);
        return this;
    }

    public ButtonModelBuilder withStyle(Object v) {
        this.model.newProperty(ButtonModel.PROP_STYLE, new Value(v), true);
        return this;
    }

    public ButtonModelBuilder withTextFont(Object v) {
        this.model.newProperty(ButtonModel.PROP_TEXTFONT, new Value(v), true);
        return this;
    }

    public ButtonModelBuilder withTextSize(Object v) {
        this.model.newProperty(ButtonModel.PROP_TEXTSIZE, new Value(v), true);
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
