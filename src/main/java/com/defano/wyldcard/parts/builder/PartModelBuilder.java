package com.defano.wyldcard.parts.builder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public abstract class PartModelBuilder<T extends PartModel, B extends PartModelBuilder> {

    protected final ExecutionContext context = new ExecutionContext();

    public abstract T build();
    public abstract B getBuilder();

    public B withId(Object id) {
        build().newProperty(PartModel.PROP_ID, new Value(id), true);
        return getBuilder();
    }

    public B withScript(Object script) {
        build().setKnownProperty(context, PartModel.PROP_SCRIPT, new Value(script));
        return getBuilder();
    }

    public B withName(Object name) {
        build().setKnownProperty(context, PartModel.PROP_NAME, new Value(name));
        return getBuilder();
    }

    public B withContents(Object contents) {
        build().setKnownProperty(context, PartModel.PROP_CONTENTS, new Value(contents));
        return getBuilder();
    }

    public B withIsVisible(Object isVisible) {
        build().setKnownProperty(context, PartModel.PROP_VISIBLE, new Value(isVisible));
        return getBuilder();
    }

    public B withTop(Object top) {
        build().setKnownProperty(context, PartModel.PROP_TOP, new Value(top));
        return getBuilder();
    }

    public B withLeft(Object left) {
        build().setKnownProperty(context, PartModel.PROP_LEFT, new Value(left));
        return getBuilder();
    }

    public B withHeight(Object height) {
        build().setKnownProperty(context, PartModel.PROP_HEIGHT, new Value(height));
        return getBuilder();
    }

    public B withWidth(Object width) {
        build().setKnownProperty(context, PartModel.PROP_WIDTH, new Value(width));
        return getBuilder();
    }

}
