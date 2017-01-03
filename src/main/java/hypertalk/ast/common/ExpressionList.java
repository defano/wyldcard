/**
 * ExpressionList.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Encapsulation of a function's argument list. Arguments in the
 * list are not evaluated until the function is called.
 */

package hypertalk.ast.common;

import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.util.ArrayList;
import java.util.List;

public class ExpressionList {

    private final List<Expression> list = new ArrayList<>();

    public ExpressionList() {
    }

    public ExpressionList(Expression expr) {
        list.add(expr);
    }

    public ExpressionList addArgument(Expression expr) {
        list.add(expr);
        return this;
    }

    public List<Value> evaluate() throws HtSemanticException {

        List<Value> evaluatedList = new ArrayList<>();

        for (Expression expr : list) {
            evaluatedList.add(expr.evaluate());
        }

        return evaluatedList;
    }

    public int getArgumentCount() {
        return list.size();
    }
}
