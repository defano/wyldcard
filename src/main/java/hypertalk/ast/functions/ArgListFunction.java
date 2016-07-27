package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.util.List;

public abstract class ArgListFunction extends Expression {

    private final ArgumentList argumentList;
    private final Expression expression;

    public ArgListFunction(ArgumentList argumentList) {
        this.argumentList = argumentList;
        this.expression = null;
    }

    public ArgListFunction(Expression expression) {
        this.expression = expression;
        this.argumentList = null;
    }

    public List<Value> evaluateArgumentList() throws HtSemanticException {
        if (expression != null) {
            return expression.evaluate().listValue();
        } else {
            return argumentList.getEvaluatedList();
        }
    }

}
