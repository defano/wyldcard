package com.defano.wyldcard.parts.builder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.stack.StackModel;

import java.awt.image.BufferedImage;

public class BackgroundModelBuilder extends PartModelBuilder<BackgroundModel, BackgroundModelBuilder> {

    private final BackgroundModel model;

    public BackgroundModelBuilder(StackModel stackModel) {
        model = new BackgroundModel(stackModel);
    }

    public BackgroundModelBuilder withCantDelete(Object v) {
        model.set(context, CardModel.PROP_CANTDELETE, new Value(v));
        return this;
    }

    public BackgroundModelBuilder withDontSearch(Object v) {
        model.set(context, CardModel.PROP_DONTSEARCH, new Value(v));
        return this;
    }

    public BackgroundModelBuilder withShowPict(Object v) {
        model.set(context, CardModel.PROP_SHOWPICT, new Value(v));
        return this;
    }

    public BackgroundModelBuilder withImage(BufferedImage v) {
        model.setBackgroundImage(v);
        return this;
    }

    @Override
    public BackgroundModel build() {
        return model;
    }

    @Override
    public BackgroundModelBuilder getBuilder() {
        return this;
    }
}
