/**
 * UserFunction.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a user defined function definition
 */

package hypertalk.ast.functions;

import hypertalk.ast.statements.StatementList;
import java.io.Serializable;

public class UserFunction implements Serializable {
private static final long serialVersionUID = -8311545370557670581L;

	public final String name;
	public final ParameterList parameters;
	public final StatementList statements;
	
	public UserFunction (String function, ParameterList parameters, StatementList statements) {
		this.name = function;
		this.parameters = parameters;
		this.statements = statements;
	}
}
