/**
 * PartIdSpecifier.java
 * @author matt.defano@gmail.com
 * 
 * ID-based specification of a part, for example "field id 22"
 */

package hypertalk.ast.containers;

import hypertalk.ast.common.PartType;

import java.io.Serializable;


public class PartIdSpecifier extends PartSpecifier implements Serializable {
private static final long serialVersionUID = 6013275870535874601L;

	public final PartType type;
	public final String id;
	
	public PartIdSpecifier(PartType type, String id) {
		this.type = type;
		this.id = id;
	}
	
	public PartType type () {
		return type;
	}
	
	public String value () {
		return id;
	}
	
	public String toString () {
		return type + " id " + id;
	}
}
