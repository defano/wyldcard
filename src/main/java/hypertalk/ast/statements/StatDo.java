/**
 * StatDo.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the "do" statement (for executing strings)
 */

package hypertalk.ast.statements;

import hypercard.runtime.Interpreter;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class StatDo extends Statement implements Serializable {
private static final long serialVersionUID = 1314153559792553421L;

	public final Expression script;
	
	public StatDo(Expression script) {
		this.script = script;
	}
	
	public void execute () throws HtSyntaxException {
		Interpreter.execute(script.evaluate().toString());
	}
}
