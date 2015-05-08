/**
 * Statement.java
 * @author matt.defano@gmail.com
 * 
 * Superclass of all statements
 */

package hypertalk.ast.statements;

import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class Statement implements Serializable {
private static final long serialVersionUID = 6925751727561632886L;

	// Set by a return statement to indicate that the remainder of the statement
	// list should not execute. 
	public boolean breakExecution = false;
	
	public void execute() throws HtSyntaxException {
		throw new HtSyntaxException("Unimplemented execution for statement");
	}
}
