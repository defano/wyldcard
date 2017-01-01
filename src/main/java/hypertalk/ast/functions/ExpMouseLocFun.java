/**
 * ExpMouseLocFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the mouseLoc"
 */

package hypertalk.ast.functions;

import hypercard.gui.util.MouseListener;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;

public class ExpMouseLocFun extends Expression {

    public ExpMouseLocFun () {}
    
    public Value evaluate () {
        return new Value(MouseListener.getMouseLoc());
    }
}
