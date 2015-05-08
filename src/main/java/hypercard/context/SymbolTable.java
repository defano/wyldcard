/**
 * SymbolTable.java
 * @author matt.defano@motorola.com
 * 
 * Implements a list of symbols (variables) and methods for getting and setting
 * their value.
 */

package hypercard.context;

import hypertalk.ast.common.Value;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable implements Serializable {
private static final long serialVersionUID = -7770752727732751367L;

	private Map<String, Value> table;
	
	public SymbolTable () {
		table = new HashMap<String, Value>();
	}
	
	public Value get (String id) {
		Value v = table.get(id);
		if (v == null)
			return new Value();
		return v;
	}
	
	public void put (String id, Value v) {
		table.put(id, v);
	}
	
	public boolean exists (String id) {
		return table.containsKey(id);
	}
}
