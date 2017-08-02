/*
 * AbstractPartModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.*;
import com.defano.hypertalk.ast.containers.PartIdSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.annotation.PostConstruct;
import java.awt.*;

/**
 * Implements a table of model associated with a partSpecifier object. Provides methods for defining, getting and
 * setting model, as well as notifying listeners of changes.
 */
public abstract class PartModel extends PropertiesModel {

    public static final String PROP_SCRIPT = "script";
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_LEFT = "left";
    public static final String PROP_TOP = "top";
    public static final String PROP_RIGHT = "right";
    public static final String PROP_BOTTOM = "bottom";
    public static final String PROP_WIDTH = "width";
    public static final String PROP_HEIGHT = "height";
    public static final String PROP_RECT = "rect";
    public static final String PROP_RECTANGLE = "rectangle";
    public static final String PROP_TOPLEFT = "topleft";
    public static final String PROP_BOTTOMRIGHT = "bottomright";
    public static final String PROP_VISIBLE = "visible";
    public static final String PROP_LOC = "loc";
    public static final String PROP_LOCATION = "location";

    private static final String PROP_SCRIPTTEXT = "scripttext";

    private final PartType type;
    private final Owner owner;
    private transient Script script = new Script();

    /**
     * Gets the name of the property that is read or written when a value is placed into the part (i.e., 'the contents'
     * or 'the text')
     * @return The name of the value property
     */
    public abstract String getValueProperty();

    protected PartModel(PartType type, Owner owner) {
        super();

        this.type = type;
        this.owner = owner;

        defineProperty(PROP_VISIBLE, new Value(true), false);
        defineProperty(PROP_SCRIPTTEXT, new Value(""), false);

        initialize();
    }

    @PostConstruct
    public void initialize() {
        super.initialize();

        // Convert rectangle (consisting of top left and bottom right coordinates) into top, left, height and width
        defineComputedSetterProperty(PROP_RECT, (model, propertyName, value) -> {
            if (value.isRect()) {
                model.setKnownProperty(PROP_LEFT, value.getItemAt(0));
                model.setKnownProperty(PROP_TOP, value.getItemAt(1));
                model.setKnownProperty(PROP_HEIGHT, new Value(value.getItemAt(3).longValue() - value.getItemAt(1).longValue()));
                model.setKnownProperty(PROP_WIDTH, new Value(value.getItemAt(2).longValue() - value.getItemAt(0).longValue()));
            } else {
                throw new HtSemanticException("Expected a rectangle, but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_RECT, (model, propertyName) -> {
            Value left = model.getKnownProperty(PROP_LEFT);
            Value top = model.getKnownProperty(PROP_TOP);
            Value height = model.getKnownProperty(PROP_HEIGHT);
            Value width = model.getKnownProperty(PROP_WIDTH);

            return new Value(left.integerValue(), top.integerValue(), left.integerValue() + width.integerValue(), top.integerValue() + height.integerValue());
        });

        defineComputedGetterProperty(PROP_RIGHT, (model, propertyName) ->
                new Value(model.getKnownProperty(PROP_LEFT).integerValue() + model.getKnownProperty(PROP_WIDTH).integerValue())
        );

        defineComputedSetterProperty(PROP_RIGHT, (model, propertyName, value) ->
                model.setKnownProperty(PROP_LEFT, new Value(value.integerValue() - model.getKnownProperty(PROP_WIDTH).integerValue()))
        );

        defineComputedGetterProperty(PROP_BOTTOM, (model, propertyName) ->
                new Value(model.getKnownProperty(PROP_TOP).integerValue() + model.getKnownProperty(PROP_HEIGHT).integerValue())
        );

        defineComputedSetterProperty(PROP_BOTTOM, (model, propertyName, value) ->
                model.setKnownProperty(PROP_TOP, new Value(value.integerValue() - model.getKnownProperty(PROP_HEIGHT).integerValue()))
        );

        defineComputedSetterProperty(PROP_TOPLEFT, (model, propertyName, value) -> {
            if (value.isPoint()) {
                model.setKnownProperty(PROP_LEFT, value.getItemAt(0));
                model.setKnownProperty(PROP_TOP, value.getItemAt(1));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_TOPLEFT, (model, propertyName) ->
                new Value(model.getKnownProperty(PROP_LEFT).integerValue(), model.getKnownProperty(PROP_TOP).integerValue())
        );

        defineComputedSetterProperty(PROP_BOTTOMRIGHT, (model, propertyName, value) -> {
            if (value.isPoint()) {
                model.setKnownProperty(PROP_LEFT, new Value(value.getItemAt(0).longValue() - model.getKnownProperty(PROP_WIDTH).longValue()));
                model.setKnownProperty(PROP_TOP, new Value(value.getItemAt(1).longValue() - model.getKnownProperty(PROP_HEIGHT).longValue()));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.stringValue());
            }
        });

        defineComputedGetterProperty(PROP_BOTTOMRIGHT, (model, propertyName) ->
                new Value(
                        model.getKnownProperty(PROP_LEFT).integerValue() + model.getKnownProperty(PROP_WIDTH).integerValue(),
                        model.getKnownProperty(PROP_TOP).integerValue() + model.getKnownProperty(PROP_HEIGHT).integerValue()
                )
        );

        definePropertyAlias(PROP_LOCATION, PROP_LOC);
        defineComputedGetterProperty(PROP_LOCATION, (model, propertyName) ->
                new Value(
                        model.getKnownProperty(PROP_LEFT).integerValue() + model.getKnownProperty(PROP_WIDTH).integerValue() / 2,
                        model.getKnownProperty(PROP_TOP).integerValue() + model.getKnownProperty(PROP_HEIGHT).integerValue() / 2
                )
        );
        defineComputedSetterProperty(PROP_LOCATION, (model, propertyName, value) -> {
            if (value.isPoint()) {
                model.setKnownProperty(PROP_LEFT, new Value(value.getItemAt(0).longValue() - model.getKnownProperty(PROP_WIDTH).longValue() / 2));
                model.setKnownProperty(PROP_TOP, new Value(value.getItemAt(1).longValue() - model.getKnownProperty(PROP_HEIGHT).longValue() / 2));
            } else {
                throw new HtSemanticException("Expected a point, but got " + value.stringValue());
            }
        });

        definePropertyAlias(PROP_RECT, PROP_RECTANGLE);

        defineComputedGetterProperty(PROP_SCRIPT, (model, propertyName) -> model.getKnownProperty(PROP_SCRIPTTEXT));
        defineComputedSetterProperty(PROP_SCRIPT, (model, propertyName, value) -> {
            model.setKnownProperty(PROP_SCRIPTTEXT, value);
            try {
                script = Interpreter.compile(value.stringValue());
            } catch (HtException e) {
                script = new Script();
            }
        });
    }

    public Rectangle getRect() {
        try {
            Rectangle rect = new Rectangle();
            rect.x = getProperty(PROP_LEFT).integerValue();
            rect.y = getProperty(PROP_TOP).integerValue();
            rect.height = getProperty(PROP_HEIGHT).integerValue();
            rect.width = getProperty(PROP_WIDTH).integerValue();

            return rect;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get geometry for part model.", e);
        }
    }

    public PartType getType() {
        return type;
    }

    public Script getScript() {
        if (script == null) {
            try {
                script = Interpreter.compile(getKnownProperty(PROP_SCRIPTTEXT).stringValue());
            } catch (HtException e) {
                e.printStackTrace();
            }
        }
        return script;
    }

    public Owner getOwner() {
         return owner;
    }

    public int getId() {
        return getKnownProperty(PROP_ID).integerValue();
    }

    public String getName() {
        return getKnownProperty(PROP_NAME).stringValue();
    }

    /**
     * Sets the value of this part; thus, sets the value of the property returned by {@link #getValueProperty()}.
     * @param value The value of this part.
     */
    public void setValue(Value value) {
        try {
            setProperty(getValueProperty(), value);
        } catch (Exception e) {
            throw new RuntimeException("Bug! Part's value cannot be set.");
        }
    }

    /**
     * Gets the value of this part; thus, reads the value of the property returned by {@link #getValueProperty()}.
     * @return The value of this property
     */
    public Value getValue() {
        return getKnownProperty(getValueProperty());
    }

    /**
     * Sends a message (i.e., 'mouseUp') to this part.
     * @param message The message to be passed.
     */
    public void sendMessage(String message) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE) {
            Interpreter.executeHandler(new PartIdSpecifier(getOwner(), getType(), getId()), getScript(), message);
        }
    }

    /**
     * Attempts to execute a function defined in the part's script.
     * @param function The name of the function to execute.
     * @param arguments The arguments to the function.
     * @return The value returned by the function upon completion.
     * @throws HtSemanticException Thrown if a syntax or semantic error occurs attempting to execute the function.
     */
    public Value executeUserFunction(String function, ExpressionList arguments) throws HtSemanticException {
        return Interpreter.executeFunction(new PartIdSpecifier(getOwner(), getType(), getId()), getScript().getFunction(function), arguments);
    }

}
