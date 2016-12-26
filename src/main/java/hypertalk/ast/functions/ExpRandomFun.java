package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.util.Random;

public class ExpRandomFun extends ArgListFunction {

    public ExpRandomFun(Expression bound) {
        super(bound);
    }

    public ExpRandomFun(ArgumentList argumentList) {
        super(argumentList);
    }

    @Override
    public Value evaluate() throws HtSemanticException {

        Value boundValue = evaluateSingleArgumentList();

        if (boundValue.isNatural()) {
            return new Value(new Random().nextInt(boundValue.integerValue()));
        } else {
            throw new HtSemanticException("Random bound must be a non-negative integer. Got " + boundValue.stringValue());
        }
    }
}
