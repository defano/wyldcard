/**
 * Ordinal.java
 * @author matt.defano@gmail.com
 * 
 * Enumeration of ordinal and relational positions.
 */

package hypertalk.ast.common;

import java.io.Serializable;

public enum Ordinal implements Serializable {
	FIRST(1), SECOND(2), THIRD(3), FOURTH(4), FIFTH(5), SIXTH(6), SEVENTH(7), 
	EIGTH(8), NINTH(9), TENTH(10), 
	
	// MAX_VALUE - 2 is required to support "after the last char of..."
	LAST(Integer.MAX_VALUE - 2),
	
	// Any negative value is interpreter to mean middle
	MIDDLE(-1);
	
	private int value;
	
	private Ordinal (int v) {
		value = v;
	}
	
	public String stringValue() {
		return value().toString();
	}
	
	public Value value() {
		return new Value(value);
	}
	
	public int intValue () {
		return value;
	}
}
