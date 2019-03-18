package com.defano.wyldcard.parts.builder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

public abstract class PartModelBuilder<T extends PartModel, B extends PartModelBuilder> {

    public abstract T build();
    public abstract B getBuilder();

    public B withScript(Object script) {
        build().setKnownProperty(new ExecutionContext(), PartModel.PROP_SCRIPT, new Value(script), false);
        return getBuilder();
    }

    public B withId(Object id) {
        build().newProperty(PartModel.PROP_ID, new Value(id), true);
        return getBuilder();
    }

    public B withName(Object name) {
        build().newProperty(PartModel.PROP_NAME, new Value(name), false);
        return getBuilder();
    }

    public B withContents(Object contents) {
        build().newProperty(PartModel.PROP_CONTENTS, new Value(contents), false);
        return getBuilder();
    }

    public B withIsVisible(Object isVisible) {
        build().newProperty(PartModel.PROP_VISIBLE, new Value(isVisible), false);
        return getBuilder();
    }

    public B withTop(Object top) {
        build().newProperty(PartModel.PROP_TOP, new Value(top), false);
        return getBuilder();
    }

    public B withLeft(Object left) {
        build().newProperty(PartModel.PROP_LEFT, new Value(left), false);
        return getBuilder();
    }

    public B withHeight(Object height) {
        build().newProperty(PartModel.PROP_HEIGHT, new Value(height), false);
        return getBuilder();
    }

    public B withWidth(Object width) {
        build().newProperty(PartModel.PROP_WIDTH, new Value(width), false);
        return getBuilder();
    }

//    public static final String PROP_SCRIPT = "script";
//    public static final String PROP_ID = "id";
//    public static final String PROP_NUMBER = "number";
//    public static final String PROP_NAME = "name";
//    public static final String PROP_LEFT = "left";
//    public static final String PROP_TOP = "top";
//    public static final String PROP_RIGHT = "right";
//    public static final String PROP_BOTTOM = "bottom";
//    public static final String PROP_WIDTH = "width";
//    public static final String PROP_HEIGHT = "height";
//    public static final String PROP_RECT = "rect";
//    public static final String PROP_RECTANGLE = "rectangle";
//    public static final String PROP_TOPLEFT = "topleft";
//    public static final String PROP_BOTTOMRIGHT = "bottomright";
//    public static final String PROP_BOTRIGHT = "botright";
//    public static final String PROP_VISIBLE = "visible";
//    public static final String PROP_LOC = "loc";
//    public static final String PROP_LOCATION = "location";
//    public static final String PROP_CONTENTS = "contents";
//    public static final String PROP_SCRIPTTEXT = "scripttext";
//    public static final String PROP_BREAKPOINTS = "breakpoints";
//    public static final String PROP_CHECKPOINTS = "checkpoints";



}
