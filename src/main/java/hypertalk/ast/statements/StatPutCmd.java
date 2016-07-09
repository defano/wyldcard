/**
 * StatPutCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "put" command
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;
import hypertalk.ast.containers.Destination;
import hypertalk.ast.containers.DestinationMsgBox;
import hypertalk.ast.containers.DestinationPart;
import hypertalk.ast.containers.DestinationVariable;
import hypertalk.ast.containers.Preposition;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.io.Serializable;

public class StatPutCmd extends Statement implements Serializable {
private static final long serialVersionUID = 2182347647936086216L;

	public final Expression expression;
	public final Preposition preposition;
	public final Destination destination;
	
	public StatPutCmd (Expression e, Preposition p, Destination d) {
		expression = e;
		preposition = p;
		destination = d;
	}
	
	public void execute () throws HtSemanticException {
		
		if (destination instanceof DestinationVariable)
			GlobalContext.getContext().put(expression.evaluate(), preposition, (DestinationVariable)destination);
		else if (destination instanceof DestinationPart)
			GlobalContext.getContext().put(expression.evaluate(), preposition, (DestinationPart)destination);
		else if (destination instanceof DestinationMsgBox)
			GlobalContext.getContext().put(expression.evaluate(), preposition, (DestinationMsgBox)destination);
		else
			throw new RuntimeException("Unknown destination type");
	}
}
