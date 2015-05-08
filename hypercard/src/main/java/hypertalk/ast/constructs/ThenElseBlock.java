/**
 * ThenElseBlock.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulates the then and else branches of a conditional statement.
 */

package hypertalk.ast.constructs;

import hypertalk.ast.statements.StatementList;
import java.io.Serializable;

public class ThenElseBlock implements Serializable {
private static final long serialVersionUID = 3000630718175894912L;

	public final StatementList thenBranch;
	public final StatementList elseBranch;
	
	public ThenElseBlock (StatementList thenBranch, StatementList elseBranch) {
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}
}
