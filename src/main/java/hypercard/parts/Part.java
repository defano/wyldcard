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
import hypertalk.ast.functions.ArgumentList;
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;
import hypertalk.properties.Properties;

public interface Part {
	
	public PartType getType();
	public String getName();
	public String getId();
	public JComponent getComponent();
	
	public Properties getProperties();
	public Value getProperty (String property) throws NoSuchPropertyException;
	public void setProperty (String property, Value value) throws NoSuchPropertyException, PropertyPermissionException, HtSemanticException;
	
	public Value getValue();
	public void setValue(Value v);
		
	public void sendMessage(String message) throws HtSemanticException;
	public Value executeUserFunction(String function, ArgumentList arguments) throws HtSemanticException;
    
    public void partOpened();
}
