package com.defano.wyldcard.parts.builder;

import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.stack.StackModel;

public class StackModelBuilder extends PartModelBuilder<StackModel, StackModelBuilder> {

    private final StackModel model;

    public StackModelBuilder() {
        model = new StackModel();
    }

    public StackModelBuilder withInitialCard() {
        model.addCard(new CardModelBuilder(model)
                .withId(model.getNextCardId())
                .withBackgroundId(model.newBackgroundModel())
                .build());

        return this;
    }

    @Override
    public StackModel build() {
        return model;
    }

    @Override
    public StackModelBuilder getBuilder() {
        return this;
    }
}
