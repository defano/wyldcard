/**
 * ExpResultsFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the result"
 */

package hypertalk.ast.functions;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import java.io.Serializable;

public class ExpResultFun extends Expression implements Serializable {
private static final long serialVersionUID = 6893977986158292297L;

	public ExpResultFun () {}
	
	public Value evaluate () {
		return GlobalContext.getContext().getIt();
	}
}
