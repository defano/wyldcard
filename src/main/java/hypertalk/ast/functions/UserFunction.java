/**
 * UserFunction.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a user defined function definition
 */

package hypertalk.ast.functions;

import hypertalk.ast.statements.StatementList;
import java.io.Serializable;

public class UserFunction {

	public final String name;
	public final ParameterList parameters;
	public final StatementList statements;
	
	public UserFunction (String function, ParameterList parameters, StatementList statements) {
		this.name = function;
		this.parameters = parameters;
		this.statements = statements;
	}
}
