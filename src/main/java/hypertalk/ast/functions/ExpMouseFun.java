/**
 * ExpMouseFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the mouse"
 */

package hypertalk.ast.functions;

import hypercard.gui.util.MouseListener;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;

public class ExpMouseFun extends Expression {

    public ExpMouseFun () {}
    
    public Value evaluate () {
        return MouseListener.isMouseDown() ? new Value("down") : new Value("up");
    }
}
