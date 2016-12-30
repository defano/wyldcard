/**
 * ArgumentList.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a function's argument list. Arguments in the 
 * list are not evaluated until the function is called. 
 */

package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.util.List;
import java.util.Vector;

public class ArgumentList {

    private List<Expression> list;
    private List<Value> evaluatedList;
    
    public ArgumentList () {
        list = new Vector<>();
    }
    
    public ArgumentList (Expression expr) {
        list = new Vector<>();
        list.add(expr);
    }
    
    public ArgumentList addArgument(Expression expr) {
        list.add(expr);
        return this;
    }
    
    public void evaluate () throws HtSemanticException {
        evaluatedList = new Vector<>();

        for (Expression expr : list)
            evaluatedList.add(expr.evaluate());
    }

    public List<Value> evaluateAndGetList() throws HtSemanticException {
        evaluate();
        return evaluatedList;
    }

    public List<Value> getEvaluatedList() throws HtSemanticException {
        return evaluatedList;
    }

    public int getArgumentCount () {
        return list.size();
    }
}
