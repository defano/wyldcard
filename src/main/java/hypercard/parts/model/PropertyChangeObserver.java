/**
 * PropertyChangeObserver.java
 * @author matt.defano@gmail.com
 * 
 * Interface allowing an object to receive notification when a part's model
 * have changed.
 */

package hypercard.parts.model;

import hypertalk.ast.common.Value;

public interface PropertyChangeObserver {
    void onPropertyChanged(String property, Value oldValue, Value newValue);
}