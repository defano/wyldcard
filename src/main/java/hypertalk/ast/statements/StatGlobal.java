/**
 * StatGlobal.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the global variable declaration statement
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;

public class StatGlobal extends Statement {

	public final String symbol;
	
	public StatGlobal (String symbol) {
		this.symbol = symbol;
	}

	public void execute () {
		GlobalContext.getContext().defineGlobal(symbol);
	}
}
