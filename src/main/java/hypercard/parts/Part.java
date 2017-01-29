/**
 * Part.java
 * @author matt.defano@gmail.com
 * 
 * Part interface; defines the set of methods common to all parts. 
 */

package hypercard.parts;

import javax.swing.*;

import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;
import hypertalk.ast.common.ExpressionList;
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;
import hypercard.parts.model.AbstractPartModel;

import java.awt.*;
import java.util.*;

public interface Part {
    
    PartType getType();
    String getName();
    int getId();
    JComponent getComponent();
    CardPart getParentCard();

    AbstractPartModel getPartModel();
    Value getProperty (String property) throws NoSuchPropertyException;
    void setProperty (String property, Value value) throws NoSuchPropertyException, PropertyPermissionException, HtSemanticException;

    Value getValue();
    void setValue(Value v);
        
    void sendMessage(String message) throws HtSemanticException;
    Value executeUserFunction(String function, ExpressionList arguments) throws HtSemanticException;
    
    void partOpened();
    void partClosed();
    Rectangle getRect();

    /**
     * Sets the Z-order of this part, moving all other parts
     * @param newPosition
     */
    default void setZorder(int newPosition) {
        CardPart card = getParentCard();
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
