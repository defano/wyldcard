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

public class ExpResultFun extends Expression {

    public ExpResultFun () {}
    
    public Value evaluate () {
        return GlobalContext.getContext().getIt();
    }
}
