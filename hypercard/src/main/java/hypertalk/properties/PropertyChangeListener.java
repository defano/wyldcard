/**
 * PropertyChangeListener.java
 * @author matt.defano@gmail.com
 * 
 * Interface allowing an object to receive notification when a part's properties
 * have changed.
 */

package hypertalk.properties;

import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartSpecifier;

public interface PropertyChangeListener {
	public void propertyChanged (PartSpecifier part, String property, Value oldValue, Value newValue);
}
