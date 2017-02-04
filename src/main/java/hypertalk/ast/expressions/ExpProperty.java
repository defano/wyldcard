/**
 * ExpProperty.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a property, for example "visible of button id 10"
 */

package hypertalk.ast.expressions;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PropertySpecifier;
import hypertalk.exception.HtSemanticException;

public class ExpProperty extends Expression {

    public final PropertySpecifier propertySpecifier;

    public ExpProperty (PropertySpecifier propertySpecifier) {
        this.propertySpecifier = propertySpecifier;
    }
    
    public Value evaluate () throws HtSemanticException {
        try {

            // Getting a HyperCard property
            if (propertySpecifier.isGlobalPropertySpecifier()) {
                return GlobalContext.getContext().getGlobalProperty(propertySpecifier.property);
            }

            // Getting the property of a part
            else {
                return GlobalContext.getContext().get(propertySpecifier.property, propertySpecifier.partExp.evaluateAsSpecifier());
            }
        } catch (Exception e) {
            throw new HtSemanticException("The property '" + propertySpecifier.property + "' does not exist on this part.");
        }
    }    
}
