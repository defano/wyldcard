package com.defano.wyldcard.parts.builder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.stack.StackModel;

import java.awt.image.BufferedImage;

public class CardModelBuilder extends PartModelBuilder<CardModel, CardModelBuilder> {

    private final CardModel model;

    public CardModelBuilder(StackModel parentPartModel) {
        model = new CardModel(parentPartModel);
    }

    public CardModelBuilder withBackgroundId(int backgroundId) {
        model.setBackgroundId(backgroundId);
        return this;
    }

    public CardModelBuilder withIsMarked(Object isMarked) {
        model.set(context, CardModel.PROP_MARKED, new Value(isMarked));
        return this;
    }

    public CardModelBuilder withCantDelete(Object v) {
        model.set(context, CardModel.PROP_CANTDELETE, new Value(v));
        return this;
    }

    public CardModelBuilder withDontSearch(Object v) {
        model.set(context, CardModel.PROP_DONTSEARCH, new Value(v));
        return this;
    }

    public CardModelBuilder withShowPict(Object v) {
        model.set(context, CardModel.PROP_SHOWPICT, new Value(v));
        return this;
    }

    public CardModelBuilder withImage(BufferedImage v) {
        model.setCardImage(v);
        return this;
    }

    @Override
    public CardModel build() {
        return model;
    }

    @Override
    public CardModelBuilder getBuilder() {
        return this;
    }
}
