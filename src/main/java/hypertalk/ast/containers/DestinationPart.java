/**
 * DestinationPart.java
 * @author matt.defano@gmail.com
 * 
 * Representation of a HyperCard part as a destination for Value
 */

package hypertalk.ast.containers;

import hypertalk.ast.common.Chunk;
import hypertalk.ast.expressions.ExpPart;
import java.io.Serializable;


public class DestinationPart extends Destination implements Serializable {
private static final long serialVersionUID = 4157689789825652324L;

	private final ExpPart part;
	private final Chunk chunk;
	
	public DestinationPart (ExpPart part) {
		this.part = part;
		this.chunk = null;
	}
	
	public DestinationPart (ExpPart part, Chunk chunk) {
		this.part = part;
		this.chunk = chunk;
	}

	public ExpPart part () {
		return part;
	}
	
	public Chunk chunk () {
		return chunk;
	}	
}
