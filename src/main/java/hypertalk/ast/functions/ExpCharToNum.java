/*
 * ExpCharToNum
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/18/17 9:48 AM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package hypertalk.ast.functions;

import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class ExpCharToNum extends Expression {

    public final Expression expression;

    public ExpCharToNum(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        Value evaluated = expression.evaluate();

        if (evaluated.stringValue().length() == 0) {
            throw new HtSemanticException("Expected a string value here.");
        }

        return new Value((int)evaluated.stringValue().charAt(0));
    }
}
