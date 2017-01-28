/**
 * Part.java
 * @author matt.defano@gmail.com
 * 
 * Part interface; defines the set of methods common to all parts. 
 */

package hypercard.parts;

import javax.swing.JComponent;

import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;
import hypertalk.ast.common.ExpressionList;
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;
import hypercard.parts.model.AbstractPartModel;

import java.awt.*;

public interface Part {
    
    PartType getType();
    String getName();
    int getId();
    JComponent getComponent();
    
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
}
