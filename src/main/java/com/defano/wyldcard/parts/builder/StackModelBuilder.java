package com.defano.wyldcard.parts.builder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.stack.StackModel;

public class StackModelBuilder extends PartModelBuilder<StackModel, StackModelBuilder> {

    private final StackModel model;

    public StackModelBuilder() {
        model = new StackModel();
    }

    public StackModelBuilder withInitialCard() {
        model.addCard(
                new CardModelBuilder(model)
                        .withId(model.getNextCardId())
                        .withBackgroundId(model.newBackgroundModel())
                        .build()
        );

        return getBuilder();
    }

    public StackModelBuilder withResizable(Object v) {
        model.setKnownProperty(context, StackModel.PROP_RESIZABLE, new Value(v));
        return getBuilder();
    }

    public StackModelBuilder withCantPeek(Object v) {
        model.setKnownProperty(context, StackModel.PROP_CANTPEEK, new Value(v));
        return getBuilder();
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
