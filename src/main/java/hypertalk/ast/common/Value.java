/**
 * Value.java
 * @author matt.defano@gmail.com
 * 
 * Representation of value in HyperTalk; all values are stored internally
 * as Strings and converted to integers, floats or booleans as required by
 * the expression. 
 * 
 * The Value object is immutable; once created it cannot change value. 
 */

package hypertalk.ast.common;

import java.util.List;
import java.util.Vector;

import hypertalk.ast.containers.Preposition;
import hypertalk.exception.HtSemanticException;
import hypertalk.utils.ChunkUtils;

public class Value {

	public final static String ITEM_DELIMITER = ",";
	
	private final String value;

	// Cache for known value types
	private Integer intValue;
	private Float floatValue;
	private Boolean booleanValue;
	
	public Value () {
		value = "";
		parse();
	}
	
	public Value (Object v) {

        if (v == null)
            value = "(null)";
        else
            value = v.toString();
        
		parse();
	}
	
	public Value (int v) {
		value = String.valueOf(v);
		parse();
	}
	
	public Value (float f) {
		value = String.valueOf(f);
		parse();
	}
	
	public Value (boolean v) {
		value = String.valueOf(v);
		parse();
	}
	
	private void parse () {		
		try {
			intValue = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			intValue = null;
		}

		try {
			floatValue = Float.parseFloat(value);
		} catch (NumberFormatException e) {
			floatValue = null;
		}
		
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			booleanValue = Boolean.parseBoolean(value);
		}

		// Special case: empty string is a valid int and float
		if (value.equals("")) {
			intValue = 0;
			floatValue = 0.0f;
		}
	}
	
	public boolean isInteger () {
		return intValue != null;
	}

	public boolean isFloat () {
		return floatValue != null;
	}	
	
	public boolean isNatural () {
		return isInteger() && integerValue() >= 0;
	}	

	public boolean isBoolean () {
		return booleanValue != null;
	}	
	
	public boolean isNumber () {
		return isFloat();
	}
		
	public String stringValue () {
		return value;
	}	
	
	public int integerValue () {
		if (intValue != null)
			return intValue;
		else
			return 0;
	}
		
	public float floatValue () {
		if (floatValue != null)
			return floatValue;
		else
			return 0.0f;
	}
		
	public boolean booleanValue () {
		if (booleanValue != null)
			return booleanValue;
		else
			return false;
	}
			
	public List<Value> listValue () {
		List<Value> list = new Vector<>();
		
		for (String item : value.split(ITEM_DELIMITER))
			list.add(new Value(item));
			
		return list;
	}
	
	public int itemCount () {
		return listValue().size();
	}
	
	public int wordCount () {
		return value.split("\\s").length;
	}
	
	public int charCount () {
		return value.split("").length - 1;
	}
	
	public int lineCount () {
		return value.split("\n").length;
	}
	
	public Value getChunk (Chunk c) throws HtSemanticException {
		
		Value startVal = null;
		Value endVal = null;
		
		int startIdx = 0;
		int endIdx = 0;
		
		if (c.start != null)
			startVal = c.start.evaluate();
		if (c.end != null)
			endVal = c.end.evaluate();
						
		if (!startVal.isNatural() && !startVal.equals(Ordinal.MIDDLE.value()))
			throw new HtSemanticException("Chunk specifier requires natural integer value, got '" + startVal + "' instead");
		if (endVal != null && !endVal.isNatural() && !endVal.equals(Ordinal.MIDDLE.value()))
			throw new HtSemanticException("Chunk specifier requires natural integer value, got '" + endVal + "' instead");

		if (startVal != null)
			startIdx = startVal.integerValue();		
		if (endVal != null)
			endIdx = endVal.integerValue();
		
		switch (c.type) {
		case CHAR:		return new Value(ChunkUtils.getChar(value, startIdx));
		case WORD:		return new Value(ChunkUtils.getWord(value, startIdx));
		case ITEM:		return new Value(ChunkUtils.getItem(value, startIdx));
		case LINE:		return new Value(ChunkUtils.getLine(value, startIdx));
		case CHARRANGE:	return new Value(ChunkUtils.getCharRange(value, startIdx, endIdx));
		case WORDRANGE:	return new Value(ChunkUtils.getWordRange(value, startIdx, endIdx));
		case ITEMRANGE: return new Value(ChunkUtils.getItemRange(value, startIdx, endIdx));
		case LINERANGE:	return new Value(ChunkUtils.getLineRange(value, startIdx, endIdx));
		default: throw new RuntimeException("Value.chunkValue()| Unhandled chunk type");
		}
	}

	public static Value setChunk (Value mutable, Preposition p, Chunk c, Object mutator) throws HtSemanticException {
		String mutatorString = mutator.toString();
		String mutableString = mutable.toString();

		Value startVal = null;
		Value endVal = null;

		int startIdx = 0;
		int endIdx = 0;
		
		if (c.start != null)
			startVal = c.start.evaluate();
		if (c.end != null)
			endVal = c.end.evaluate();
		
		if (!startVal.isNatural() && !startVal.equals(Ordinal.MIDDLE.value()))
			throw new HtSemanticException("Chunk specifier requires natural integer value, got '" + startVal + "' instead");
		if (endVal != null && !endVal.isNatural() && !endVal.equals(Ordinal.MIDDLE.value()))
			throw new HtSemanticException("Chunk specifier requires natural integer value, got '" + endVal + "' instead");
		
		if (startVal != null)
			startIdx = startVal.integerValue();
		if (endVal != null)
			endIdx = endVal.integerValue();
		
		switch (c.type) {
		case CHAR:		mutableString = ChunkUtils.setChar(p, mutableString, startIdx, mutatorString); break;
		case WORD:		mutableString = ChunkUtils.setWord(p, mutableString, startIdx, mutatorString); break;
		case ITEM:		mutableString = ChunkUtils.setItem(p, mutable, startIdx, mutatorString); break;
		case LINE: 		mutableString = ChunkUtils.setLine(p, mutableString, startIdx, mutatorString); break;
		case CHARRANGE:	mutableString = ChunkUtils.setCharRange(p, mutableString, startIdx, endIdx, mutatorString); break;
		case WORDRANGE:	mutableString = ChunkUtils.setWordRange(p, mutableString, startIdx, endIdx, mutatorString); break;
		case ITEMRANGE: mutableString = ChunkUtils.setItemRange(p, mutable, startIdx, endIdx, mutatorString); break;
		case LINERANGE:	mutableString = ChunkUtils.setLineRange(p, mutableString, startIdx, endIdx, mutatorString); break;
		default: throw new RuntimeException("Value.setChunk()| Unhandled chunk type");
		}
		
		return new Value(mutableString);
	}
	
	public static Value setValue (Value mutable, Preposition p, Value mutator) {
		
		switch (p) {
		case BEFORE:	return new Value(mutator.toString() + mutable.toString());
		case INTO:		return new Value(mutator.toString());
		case AFTER:		return new Value(mutable.toString() + mutator.toString());
		default: throw new RuntimeException("Value.setValue()| Unhandeled preposition");
		}
	}
	
	public boolean isEmpty () {
		return value.equals("");
	}
	
	public boolean equals (Object v) {
		return value.equals(v.toString());
	}

	public Value lessThan (Object val) {
		Value v = new Value(val);
		if (isFloat() && v.isFloat())
			return new Value(floatValue() < v.floatValue());
		else
			return new Value(toString().compareTo(v.toString()) < 0);
	}

	public Value greaterThan (Object val) {
		Value v = new Value(val);
		if (isFloat() && v.isFloat())
			return new Value(floatValue() > v.floatValue());
		else
			return new Value(toString().compareTo(v.toString()) > 0);
	}
	
	public Value greaterThanOrEqualTo (Object val) {
		Value v = new Value(val);
		if (isFloat() && v.isFloat())
			return new Value (floatValue() >= v.floatValue());
		else
			return new Value(toString().compareTo(v.toString()) >= 0);
	}
	
	public Value lessThanOrEqualTo (Object val) {
		Value v = new Value(val);
		if (isFloat() && v.isFloat())
			return new Value(floatValue() <= v.floatValue());
		else
			return new Value(toString().compareTo(v.toString()) <= 0);
	}
	
	public Value multiply (Object val) throws HtSemanticException {
		Value v = new Value(val);
		if (!isNumber())
			throw new HtSemanticException(value + " cannot be multiplied because it is not a number");
		if (!v.isNumber())
			throw new HtSemanticException(value + " cannot be multiplied by the text expression: " + v);
		
		if (isInteger() && v.isInteger())
			return new Value(integerValue() * v.integerValue());
		else
			return new Value(floatValue() * v.floatValue());
	}
	
	public Value divide (Object val) throws HtSemanticException {
		Value v = new Value(val);
		if (!isNumber())
			throw new HtSemanticException(value + " cannot be divided because it is not a number");
		if (!v.isNumber())
			throw new HtSemanticException(value + " cannot be divided by the text expression: " + v);
		
		if (isInteger() && v.isInteger())
			return new Value(integerValue() / v.integerValue());
		else
			return new Value(floatValue() / v.floatValue());
	}

	public Value add (Object val) throws HtSemanticException {
		Value v = new Value(val);
		if (!isNumber())
			throw new HtSemanticException(value + " cannot be added because it is not a number");
		if (!v.isNumber())
			throw new HtSemanticException(value + " cannot be added to the text expression: " + v);
		
		if (isInteger() && v.isInteger())
			return new Value(integerValue() + v.integerValue());
		else
			return new Value(floatValue() + v.floatValue());
	}
	
	public Value subtract (Object val) throws HtSemanticException {
		Value v = new Value(val);
		if (!isNumber())
			throw new HtSemanticException(value + " cannot be subtracted because it is not a number");
		if (!v.isNumber())
			throw new HtSemanticException(value + " cannot be subtracted by the text expression: " + v);
		
		if (isInteger() && v.isInteger())
			return new Value(integerValue() - v.integerValue());
		else
			return new Value(floatValue() - v.floatValue());
	}
	
	public Value exponentiate (Object val) throws HtSemanticException {
		Value v = new Value(val);
		if (!isNumber())
			throw new HtSemanticException(value + " cannot be raised to a power because it is not a number");
		if (!v.isNumber())
			throw new HtSemanticException(value + " cannot be raised to the power of the text expression: " + v);
		
		return new Value(Math.pow(floatValue(), v.floatValue()));
	}

	public Value mod (Object val) throws HtSemanticException {
		Value v = new Value(val);
		if (!isNumber())
			throw new HtSemanticException(value + " cannot be divided because it is not a number");
		if (!v.isNumber())
			throw new HtSemanticException(value + " cannot be divided by the text expression: " + v);
		
		if (isInteger() && v.isInteger())
			return new Value(integerValue() % v.integerValue());
		else
			return new Value(floatValue() % v.floatValue());
	}
	
	public Value not () throws HtSemanticException {
		if (!isBoolean())
			throw new HtSemanticException(value + " cannot be negated because it is not boolean");
		
		return new Value(!booleanValue());
	}

	public Value negate () throws HtSemanticException {
		if (!isBoolean())
			throw new HtSemanticException(value + " cannot be negated because it is not boolean");
		
		if (isInteger())
			return new Value(integerValue() * -1);
		else
			return new Value(floatValue() * -1);
	}

	public Value and (Value val) throws HtSemanticException {
		Value v = new Value(val);
		if (!isBoolean())
			throw new HtSemanticException(value + " cannot be and'ed because it is not boolean");
		if (!v.isBoolean())
			throw new HtSemanticException(value + " cannot be and'ed with text value " + v);
		
		return new Value(booleanValue() && v.booleanValue());
	}
	
	public Value or (Value val) throws HtSemanticException {
		Value v = new Value(val);
		if (!isBoolean())
			throw new HtSemanticException(value + " cannot be or'ed because it is not boolean");
		if (!v.isBoolean())
			throw new HtSemanticException(value + " cannot be or'ed with text value " + v);
		
		return new Value(booleanValue() || v.booleanValue());
	}
	
	public Value concat (Value val) {
		return new Value(value + val.toString());
	}
	
	public boolean contains (Object v) {
		return value.contains(v.toString());
	}
		
	public String toString () {
		return value;
	}
}
