/**
 * Part.java
 * @author matt.defano@gmail.com
 * 
 * Part interface; defines the set of methods common to all parts. 
 */

package hypercard.parts;

import javax.swing.*;

import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.parts.model.*;
import hypercard.runtime.Interpreter;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Script;
import hypertalk.ast.common.Value;
import hypertalk.ast.common.ExpressionList;
import hypertalk.ast.containers.PartIdSpecifier;
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;

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
        Interpreter.executeHandler(new PartIdSpecifier(getType(), getId()), getScript(), message);
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
