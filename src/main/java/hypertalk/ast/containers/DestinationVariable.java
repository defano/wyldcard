/**
 * DestinationVariable.java
 * @author matt.defano@gmail.com
 * 
 * Representation of a variable as a destination for Value
 */

package hypertalk.ast.containers;

import hypertalk.ast.common.Chunk;

import java.io.Serializable;

public class DestinationVariable extends Destination implements Serializable {
private static final long serialVersionUID = 4545545632198713937L;

	private final String symbol;
	private final Chunk chunk;
	
	public DestinationVariable (String symbol) {
		this.symbol = symbol;
		this.chunk = null;
	}
	
	public DestinationVariable (String symbol, Chunk chunk) {
		this.symbol = symbol;
		this.chunk = chunk;
	}

	public String symbol () {
		return symbol;
	}
	
	public Chunk chunk () {
		return chunk;
	}	
}
