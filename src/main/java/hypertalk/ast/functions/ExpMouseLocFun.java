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

public class ExpMouseLocFun extends Expression {

    public ExpMouseLocFun () {}
    
    public Value evaluate () {
        return RuntimeEnv.getRuntimeEnv().getTheMouseLocValue();
    }
}
