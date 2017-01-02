/**
 * ArgumentList.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Encapsulation of a function's argument list. Arguments in the
 * list are not evaluated until the function is called.
 */

package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.util.ArrayList;
import java.util.List;

public class ArgumentList {

    private final List<Expression> list = new ArrayList<>();

    public ArgumentList() {
    }

    public ArgumentList(Expression expr) {
        list.add(expr);
    }

    public ArgumentList addArgument(Expression expr) {
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
