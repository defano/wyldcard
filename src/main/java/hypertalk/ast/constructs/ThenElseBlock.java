/**
 * ThenElseBlock.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulates the then and else branches of a conditional statement.
 */

package hypertalk.ast.constructs;

import hypertalk.ast.statements.StatementList;

public class ThenElseBlock {

	public final StatementList thenBranch;
	public final StatementList elseBranch;
	
	public ThenElseBlock (StatementList thenBranch, StatementList elseBranch) {
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}
}
