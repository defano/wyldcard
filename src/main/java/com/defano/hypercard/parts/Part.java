/*
 * Part
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts;

import javax.swing.*;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Script;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.containers.PartIdSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.NoSuchPropertyException;
import com.defano.hypertalk.exception.PropertyPermissionException;

import java.awt.*;
import java.util.*;

/**
 * An interface defining common behavior to all parts.
 */
public interface Part {

    /**
     * Gets the type of this part.
     * @return The type of the part.
     */
    PartType getType();

    /**
     * Gets the Swing component associated with this part.
     * @return The Java Swing component.
     */
    JComponent getComponent();

    /**
     * Gets the card on which this part exists.
     * @return The part's parent card.
     */
    CardPart getCard();

    /**
     * Gets the data model associated with this part.
     * @return The part model.
     */
    PartModel getPartModel();

    /**
     * Gets the name of the property that is read or written when a value is placed into the part (i.e., 'the contents'
     * or 'the text')
     * @return The name of the value property
     */
    String getValueProperty();

    /**
     * Gets the HyperTalk script associated with this part.
     * @return The script text.
     */
    Script getScript();

    /**
     * Invoked when the part is opened (added) to a card.
     */
    void partOpened();

    /**
     * Invoked when the part is closed (removed) from a card.
     */
    void partClosed();

    /**
     * Determines the layer of the card on which this part exists.
     * @return The layer of the card the part is on or null if indeterminate
     */
    default CardLayer getCardLayer() {
        return getCard().getCardLayer(getComponent());
    }

    /**
     * Determines the currently active part layer, either {@link CardLayer#BACKGROUND_PARTS} or
     * {@link CardLayer#CARD_PARTS} depending on whether the user is presently editing the background.
     *
     * @return The part layer currently being edited.
     */
    static CardLayer getActivePartLayer() {
        return ToolsContext.getInstance().isEditingBackground() ? CardLayer.BACKGROUND_PARTS : CardLayer.CARD_PARTS;
    }


    /**
     * Sets the property of the part.
     * @param property The name of the property to set
     * @param value The value to which it should be set
     * @throws NoSuchPropertyException Thrown if no such property exists on this part
     * @throws PropertyPermissionException Thrown when attempting to write a read-only property (like ID)
     * @throws HtSemanticException Thrown if value provided is invalid for this property
     */
    default void setProperty(String property, Value value) throws NoSuchPropertyException, PropertyPermissionException, HtSemanticException {
        getPartModel().setProperty(property, value);
    }

    /**
     * Gets the value of a property on this part.
     * @param property The name of the property to get
     * @return The value of the property
     * @throws NoSuchPropertyException Thrown if no such property exists on the part.
     */
    default Value getProperty(String property) throws NoSuchPropertyException {
        return getPartModel().getProperty(property);
    }

    /**
     * Sets the value of this part; thus, sets the value of the property returned by {@link #getValueProperty()}.
     * @param value The value of this part.
     */
    default void setValue(Value value) {
        try {
            getPartModel().setProperty(getValueProperty(), value);
        } catch (Exception e) {
            throw new RuntimeException("Bug! Part's value cannot be set.");
        }
    }

    /**
     * Gets the value of this part; thus, reads the value of the property returned by {@link #getValueProperty()}.
     * @return The value of this property
     */
    default Value getValue() {
        return getPartModel().getKnownProperty(getValueProperty());
    }

    /**
     * Sends a message (i.e., 'mouseUp') to this part.
     * @param message The message to be passed.
     */
    default void sendMessage(String message) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE) {
            Interpreter.executeHandler(new PartIdSpecifier(getType(), getId()), getScript(), message);
        }
    }

    /**
     * Attempts to execute a function defined in the part's script.
     * @param function The name of the function to execute.
     * @param arguments The arguments to the function.
     * @return The value returned by the function upon completion.
     * @throws HtSemanticException Thrown if a syntax or semantic error occurs attempting to execute the function.
     */
    default Value executeUserFunction(String function, ExpressionList arguments) throws HtSemanticException {
        return Interpreter.executeFunction(new PartIdSpecifier(getType(), getId()), getScript().getFunction(function), arguments);
    }

    /**
     * Gets the bounds of this part.
     * @return The bounds of the part.
     */
    default Rectangle getRect() {
        return getPartModel().getRect();
    }

    /**
     * Gets the ID of this part.
     * @return The part id.
     */
    default int getId() {
        return getPartModel().getKnownProperty(PartModel.PROP_ID).integerValue();
    }

    /**
     * Gets the name of this part.
     * @return The part name.
     */
    default String getName() {
        return getPartModel().getKnownProperty(PartModel.PROP_NAME).stringValue();
    }

    /**
     * Determines if the part tool associated with editing parts of this kind is currently active (i.e., is button tool
     * active if this part is a button).
     * @return True if the part tool is active; false otherwise.
     */
    default boolean isPartToolActive() {
        return ToolsContext.getInstance().getToolMode() == (getType() == PartType.BUTTON ? ToolMode.BUTTON : ToolMode.FIELD);
    }

    /**
     * Sets the z-position of this part relative to other parts on the card.
     * @param newPosition
     */
    default void setZorder(int newPosition) {
        CardPart card = getCard();
        ArrayList<Part> parts = new ArrayList<>(card.getPartsInZOrder());

        if (newPosition < 0) {
            newPosition = 0;
        } else if (newPosition > parts.size() - 1) {
            newPosition = parts.size() - 1;
        }

        parts.remove(this);
        parts.add(newPosition, this);

        for (int index = 0; index < parts.size(); index++) {
            Part thisPart = parts.get(index);
            thisPart.getPartModel().setKnownProperty(PartModel.PROP_ZORDER, new Value(index), true);
        }

        card.onZOrderChanged();
    }

}
