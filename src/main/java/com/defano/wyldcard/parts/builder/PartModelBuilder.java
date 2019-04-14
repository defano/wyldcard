package com.defano.wyldcard.parts.builder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.*;

public abstract class PartModelBuilder<ModelType extends PartModel, BuilderType extends PartModelBuilder> {

    protected final ExecutionContext context = new ExecutionContext();

    public abstract ModelType build();
    public abstract BuilderType getBuilder();

    public BuilderType withId(Object id) {
        build().newProperty(PartModel.PROP_ID, new Value(id), true);
        return getBuilder();
    }

    public BuilderType withScript(Object script) {
        build().setKnownProperty(context, PartModel.PROP_SCRIPT, new Value(script));
        return getBuilder();
    }

    public BuilderType withName(Object name) {
        build().setKnownProperty(context, PartModel.PROP_NAME, new Value(name));
        return getBuilder();
    }

    public BuilderType withContents(Object contents) {
        build().setKnownProperty(context, PartModel.PROP_CONTENTS, new Value(contents));
        return getBuilder();
    }

    public BuilderType withIsVisible(Object isVisible) {
        build().setKnownProperty(context, PartModel.PROP_VISIBLE, new Value(isVisible));
        return getBuilder();
    }

    public BuilderType withTop(Object top) {
        build().setKnownProperty(context, PartModel.PROP_TOP, new Value(top));
        return getBuilder();
    }

    public BuilderType withLeft(Object left) {
        build().setKnownProperty(context, PartModel.PROP_LEFT, new Value(left));
        return getBuilder();
    }

    public BuilderType withHeight(Object height) {
        build().setKnownProperty(context, PartModel.PROP_HEIGHT, new Value(height));
        return getBuilder();
    }

    public BuilderType withWidth(Object width) {
        build().setKnownProperty(context, PartModel.PROP_WIDTH, new Value(width));
        return getBuilder();
    }

    public BuilderType withBounds(Rectangle rectangle) {
        withTop(rectangle.y);
        withLeft(rectangle.x);
        withWidth(rectangle.width);
        withHeight(rectangle.height);
        return getBuilder();
    }

    public BuilderType withTextFont(Object v) {
        build().setKnownProperty(context, CardLayerPartModel.PROP_TEXTFONT, new Value(v));
        return getBuilder();
    }

    public BuilderType withTextSize(Object v) {
        build().setKnownProperty(context, CardLayerPartModel.PROP_TEXTSIZE, new Value(v));
        return getBuilder();
    }

    public BuilderType withTextStyle(Object v) {
        build().setKnownProperty(context, CardLayerPartModel.PROP_TEXTSTYLE, new Value(v));
        return getBuilder();
    }

    public BuilderType withTextAlign(Object v) {
        build().setKnownProperty(context, CardLayerPartModel.PROP_TEXTALIGN, new Value(v));
        return getBuilder();
    }
}
