/**
 * PartSpecifier.java
 * @author matt.defano@gmail.com
 * 
 * Abstract superclass for part specifiers
 */

package hypertalk.ast.containers;

import hypertalk.ast.common.PartType;

import java.io.Serializable;


public abstract class PartSpecifier implements Serializable {
	private static final long serialVersionUID = -1360422079965072826L;

	abstract public String value();
	abstract public PartType type();
	abstract public String toString();
}
