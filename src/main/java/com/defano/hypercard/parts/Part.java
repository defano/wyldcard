/*
 * Part
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * Part.java
 * @author matt.defano@gmail.com
 * 
 * Part interface; defines the set of methods common to all parts. 
 */

package com.defano.hypercard.parts;

import javax.swing.*;

import com.defano.hypercard.parts.model.AbstractPartModel;
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

public interface Part {
    
    PartType getType();
    JComponent getComponent();
    CardPart getCard();
    AbstractPartModel getPartModel();
    String getValueProperty();
    Script getScript();
    void partOpened();
    void partClosed();

    default void setProperty(String property, Value value) throws NoSuchPropertyException, PropertyPermissionException, HtSemanticException {
        getPartModel().setProperty(property, value);
    }

    default Value getProperty(String property) throws NoSuchPropertyException {
        return getPartModel().getProperty(property);
    }

    default void setValue(Value value) {
        try {
            getPartModel().setProperty(getValueProperty(), value);
        } catch (Exception e) {
            throw new RuntimeException("Bug! Part's value be set");
        }
    }

    default Value getValue() {
        return getPartModel().getKnownProperty(getValueProperty());
    }

    default void sendMessage(String message) {
        if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE) {
            Interpreter.executeHandler(new PartIdSpecifier(getType(), getId()), getScript(), message);
        }
    }

    default Value executeUserFunction(String function, ExpressionList arguments) throws HtSemanticException {
        return Interpreter.executeFunction(new PartIdSpecifier(getType(), getId()), getScript().getFunction(function), arguments);
    }

    default Rectangle getRect() {
        return getPartModel().getRect();
    }

    default int getId() {
        return getPartModel().getKnownProperty(AbstractPartModel.PROP_ID).integerValue();
    }

    default String getName() {
        return getPartModel().getKnownProperty(AbstractPartModel.PROP_NAME).stringValue();
    }

    default boolean isPartToolActive() {
        return ToolsContext.getInstance().getToolMode() == (getType() == PartType.BUTTON ? ToolMode.BUTTON : ToolMode.FIELD);
    }

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
            thisPart.getPartModel().setKnownProperty(AbstractPartModel.PROP_ZORDER, new Value(index), true);
        }

        card.onZOrderChanged();
    }

}
