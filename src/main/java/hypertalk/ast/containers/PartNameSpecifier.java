/**
 * PartNameSpecifier.java
 * @author matt.defano@gmail.com
 * 
 * Name-based specification of a part, for example, "button myButton"
 */

package hypertalk.ast.containers;

import hypertalk.ast.common.PartType;

import java.io.Serializable;


public class PartNameSpecifier extends PartSpecifier implements Serializable {
private static final long serialVersionUID = -130340017416097363L;

	public final PartType type;
	public final String name;
	
	public PartNameSpecifier (PartType type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public PartType type() {
		return type;
	}
	
	public String value() {
		return name;
	}
	
	public String toString() {
		return type + " " + name;
	}
}
