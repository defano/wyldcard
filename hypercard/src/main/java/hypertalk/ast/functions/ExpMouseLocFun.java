/**
 * ExpMouseLocFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the mouseLoc"
 */

package hypertalk.ast.functions;

import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;

import java.io.Serializable;

public class ExpMouseLocFun extends Expression implements Serializable {
private static final long serialVersionUID = 2424689731981895544L;

	public ExpMouseLocFun () {}
	
	public Value evaluate () {
		return RuntimeEnv.getRuntimeEnv().getTheMouseLocValue();
	}
}
