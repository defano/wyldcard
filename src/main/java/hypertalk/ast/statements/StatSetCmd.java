/**
 * StatSetCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "set" command (for mutating a property)
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;
import hypercard.parts.PartException;
import hypertalk.ast.containers.ContainerVariable;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.containers.PropertySpecifier;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class StatSetCmd extends Statement {

    public final Expression expression;
    public final PropertySpecifier propertySpec;

    public StatSetCmd (PropertySpecifier propertySpec, Expression expression) {
        this.propertySpec = propertySpec;
        this.expression = expression;
    }
    
    public void execute () throws HtSemanticException {
        try {
            
            // Setting the property of HyperCard
            if (propertySpec.isGlobalPropertySpecifier()) {
                GlobalContext.getContext().setGlobalProperty(propertySpec.property, expression.evaluate());
            }

            // Setting the property of a part
            else {
                GlobalContext.getContext().set(propertySpec.property, propertySpec.partExp.evaluateAsSpecifier(), Preposition.INTO, null, expression.evaluate());
            }

        } catch (Exception e) {

            if (propertySpec.partExp != null) {
                throw new HtSemanticException("Cannot set the '" + propertySpec.property + "' of this part.");
            } else {
                // When all else fails, set the value of a container
                GlobalContext.getContext().put(expression.evaluate(), Preposition.INTO, new ContainerVariable(propertySpec.property));
            }
        }
    }
}
