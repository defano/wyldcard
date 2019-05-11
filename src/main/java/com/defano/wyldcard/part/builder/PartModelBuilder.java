package com.defano.wyldcard.part.builder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.part.card.CardLayerPartModel;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.awt.*;

public abstract class PartModelBuilder<ModelType extends PartModel, BuilderType extends PartModelBuilder> {

    protected final ExecutionContext context = new ExecutionContext();

    public abstract ModelType build();
    public abstract BuilderType getBuilder();

    public BuilderType withId(Object id) {
        build().define(PartModel.PROP_ID).asConstant(id);
        return getBuilder();
    }

    public BuilderType withScript(Object script) {
        build().set(context, PartModel.PROP_SCRIPT, new Value(script));
        return getBuilder();
    }

    public BuilderType withName(Object name) {
        build().set(context, PartModel.PROP_NAME, new Value(name));
        return getBuilder();
    }

    public BuilderType withPartStyle(Object v) {
        build().set(context, CardLayerPartModel.PROP_STYLE, new Value(v));
        return getBuilder();
    }

    public BuilderType withContents(Object contents) {
        build().set(context, PartModel.PROP_CONTENTS, new Value(contents));
        return getBuilder();
    }

    public BuilderType withIsVisible(Object isVisible) {
        build().set(context, PartModel.PROP_VISIBLE, new Value(isVisible));
        return getBuilder();
    }

    public BuilderType withTop(Object top) {
        build().set(context, PartModel.PROP_TOP, new Value(top));
        return getBuilder();
    }

    public BuilderType withLeft(Object left) {
        build().set(context, PartModel.PROP_LEFT, new Value(left));
        return getBuilder();
    }

    public BuilderType withHeight(Object height) {
        build().set(context, PartModel.PROP_HEIGHT, new Value(height));
        return getBuilder();
    }

    public BuilderType withWidth(Object width) {
        build().set(context, PartModel.PROP_WIDTH, new Value(width));
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
        build().set(context, CardLayerPartModel.PROP_TEXTFONT, new Value(v));
        return getBuilder();
    }

    public BuilderType withTextSize(Object v) {
        build().set(context, CardLayerPartModel.PROP_TEXTSIZE, new Value(v));
        return getBuilder();
    }

    public BuilderType withTextStyle(Object v) {
        build().set(context, CardLayerPartModel.PROP_TEXTSTYLE, new Value(v));
        return getBuilder();
    }

    public BuilderType withTextAlign(Object v) {
        build().set(context, CardLayerPartModel.PROP_TEXTALIGN, new Value(v));
        return getBuilder();
    }

    public BuilderType withPartNumber(Object v) {
        build().set(context, CardLayerPartModel.PROP_ZORDER, new Value(v));
        return getBuilder();
    }

}
