/**
 * ExpUserFunction.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a user-defined function call, for example: "myfunction(arg)"
 */

package hypertalk.ast.expressions;

import hypercard.context.GlobalContext;
import hypercard.parts.Part;
import hypercard.parts.PartException;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.exception.HtSemanticException;

public class ExpUserFunction extends Expression {

    public final String function;
    public final ArgumentList arguments;

    public ExpUserFunction (String function, ArgumentList arguments) {
        this.function = function;
        this.arguments = arguments;
    }
    
    public Value evaluate () throws HtSemanticException {
        
        try {
            PartSpecifier ps = GlobalContext.getContext().getMe();
            Part part = GlobalContext.getContext().get(ps);
            
            arguments.evaluate();
            return part.executeUserFunction(function, arguments);
        } catch (PartException e) {
            throw new HtSemanticException(e.getMessage());
        }                        
    }
}
