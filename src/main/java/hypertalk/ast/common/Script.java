/**
 * Script.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a HyperTalk script; might include user defined functions,
 * handlers, or loose statements.
 */

package hypertalk.ast.common;

import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.ast.functions.UserFunction;
import hypertalk.ast.statements.StatementList;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Script implements Serializable {
private static final long serialVersionUID = 1338999304303112852L;

	private Map<String, StatementList> handlers;
	private Map<String, UserFunction> userfunctions;
	private StatementList statements = null;
	
	public Script () {
		handlers = new HashMap<String, StatementList>();
		userfunctions = new HashMap<String, UserFunction>();
	}

	public Script defineHandler (NamedBlock handler) {
		handlers.put(handler.name, handler.body);
		return this;
	}
	
	public Script defineUserFunction (UserFunction function) {
		userfunctions.put(function.name, function);
		return this;
	}
	
	public Script defineStatementList (StatementList statements) {
		this.statements = statements;
		return this;
	}
	
	public StatementList getHandler (String handler) {
		return handlers.get(handler);
	}
	
	public UserFunction getUserFunction (String function) {
		return userfunctions.get(function);
	}
	
	public StatementList getStatementList () {
		return statements;
	}
	
	public void executeHandler (String handler) {
		if (handlers.containsKey(handler))
			RuntimeEnv.getRuntimeEnv().executeStatementList(handlers.get(handler));			
	}
	
	public void executeStatement () throws HtSyntaxException {
		if (statements != null)
			statements.execute();
	}
	
	public Value executeUserFunction (String function, ArgumentList arguments) throws HtSyntaxException {
		UserFunction theFunction = userfunctions.get(function);
		
		if (theFunction != null) 
			return RuntimeEnv.getRuntimeEnv().executeUserFunction(theFunction, arguments);
		else
			throw new HtSyntaxException("No such function " + function);
	}	
}
