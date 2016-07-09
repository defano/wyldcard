/**
 * StatGlobal.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the global variable declaration statement
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;

import java.io.Serializable;

public class StatGlobal extends Statement implements Serializable {
private static final long serialVersionUID = 169586275950963284L;

	public final String symbol;
	
	public StatGlobal (String symbol) {
		this.symbol = symbol;
	}

	public void execute () {
		GlobalContext.getContext().defineGlobal(symbol);
	}
}
