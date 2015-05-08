/**
 * PartType.java
 * @author matt.defano@gmail.com
 * 
 * Simple enumeration of part types
 */

package hypertalk.ast.common;

import java.io.Serializable;

public enum PartType implements Serializable {
	FIELD, BUTTON, MESSAGEBOX;
}
