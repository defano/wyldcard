/**
 * RepeatDuration.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "repeat while..." and "repeat until..." constructs
 */

package hypertalk.ast.constructs;

import hypertalk.ast.expressions.Expression;

public class RepeatDuration extends RepeatSpecifier {

    public static boolean POLARITY_WHILE = true;
    public static boolean POLARITY_UNTIL = false;
    
    public final boolean polarity;
    public final Expression condition;
    
    public RepeatDuration (boolean polarity, Expression condition) {
        this.polarity = polarity;
        this.condition = condition;
    }    
}
