/**
 * ExpMouseFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the mouse"
 */

package hypertalk.ast.functions;

import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import java.io.Serializable;

public class ExpMouseFun extends Expression {

	public ExpMouseFun () {}
	
	public Value evaluate () {
		return RuntimeEnv.getRuntimeEnv().getTheMouse();
	}
}
