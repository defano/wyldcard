/**
 * AbstractPartModel.java
 * @author matt.defano@gmail.com
 * 
 * Implements a table of model associated with a partSpecifier object. Provides
 * methods for defining, getting and setting model, as well as notifying
 * listeners of changes. 
 */

package hypercard.parts.model;

import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;
import hypertalk.exception.HtSemanticException;

import java.awt.*;

public abstract class AbstractPartModel extends PropertiesModel {

    public static final String PROP_SCRIPT = "script";
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_LEFT = "left";
    public static final String PROP_TOP = "top";
    public static final String PROP_WIDTH = "width";
    public static final String PROP_HEIGHT = "height";
    public static final String PROP_RECT = "rect";
    public static final String PROP_RECTANGLE = "rectangle";
    public static final String PROP_TOPLEFT = "topleft";
    public static final String PROP_BOTTOMRIGHT = "bottomright";
    public static final String PROP_VISIBLE = "visible";

    private PartType type;

    protected AbstractPartModel(PartType type) {
        this.type = type;

        // Convert rectangle (consisting of top left and bottom right coordinates) into top, left, height and width
        defineComputedSetterProperty(PROP_RECT, (model, propertyName, value) -> {
            if (value.isRect()) {
                model.setKnownProperty(PROP_LEFT, value.listItemAt(0));
                model.setKnownProperty(PROP_TOP, value.listItemAt(1));
                model.setKnownProperty(PROP_HEIGHT, new Value(value.listItemAt(3).longValue() - value.listItemAt(1).longValue()));
                model.setKnownProperty(PROP_WIDTH, new Value(value.listItemAt(2).longValue() - value.listItemAt(0).longValue()));
            } else {
                throw new HtSemanticException("Expected a rectangle but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_RECT, (model, propertyName) -> {
            Value left = model.getKnownProperty(PROP_LEFT);
            Value top = model.getKnownProperty(PROP_TOP);
            Value height = model.getKnownProperty(PROP_HEIGHT);
            Value width = model.getKnownProperty(PROP_WIDTH);

            return new Value(left.integerValue(), top.integerValue(), left.integerValue() + width.integerValue(), top.integerValue() + height.integerValue());
        });

        defineComputedSetterProperty(PROP_TOPLEFT, (model, propertyName, value) -> {
            if (value.isPoint()) {
                model.setKnownProperty(PROP_LEFT, value.listItemAt(0));
                model.setKnownProperty(PROP_TOP, value.listItemAt(1));
            } else {
                throw new HtSemanticException("Expected a point but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_TOPLEFT, (model, propertyName) ->
                new Value(model.getKnownProperty(PROP_LEFT).integerValue(), model.getKnownProperty(PROP_TOP).integerValue())
        );

        defineComputedSetterProperty(PROP_BOTTOMRIGHT, (model, propertyName, value) -> {
            if (value.isPoint()) {
                model.setKnownProperty(PROP_LEFT, new Value(value.listItemAt(0).longValue() - model.getKnownProperty(PROP_WIDTH).longValue()));
                model.setKnownProperty(PROP_TOP, new Value(value.listItemAt(1).longValue() - model.getKnownProperty(PROP_HEIGHT).longValue()));
            } else {
                throw new HtSemanticException("Expected a point but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_BOTTOMRIGHT, (model, propertyName) ->
                new Value(
                        model.getKnownProperty(PROP_LEFT).integerValue() + model.getKnownProperty(PROP_WIDTH).integerValue(),
                        model.getKnownProperty(PROP_TOP).integerValue() + model.getKnownProperty(PROP_HEIGHT).integerValue()
                )
        );

        definePropertyAlias(PROP_RECT, PROP_RECTANGLE);
        defineProperty(PROP_VISIBLE, new Value(true), false);
    }

    public PartType getType () {
        return type;
    }

    public Rectangle getRect() {
        try {
            Rectangle rect = new Rectangle();
            rect.x = getProperty(ButtonModel.PROP_LEFT).integerValue();
            rect.y = getProperty(ButtonModel.PROP_TOP).integerValue();
            rect.height = getProperty(ButtonModel.PROP_HEIGHT).integerValue();
            rect.width = getProperty(ButtonModel.PROP_WIDTH).integerValue();

            return rect;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get geometry for part model.");
        }
    }
}
