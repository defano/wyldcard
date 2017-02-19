/*
 * ExpValueFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 9:33 AM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package hypertalk.ast.functions;

import hypercard.runtime.Interpreter;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

public class ExpValueFun extends Expression {

    public final Expression expression;

    public ExpValueFun(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        String toEvaluate = expression.evaluate().stringValue();
        return Interpreter.evaluate(toEvaluate);
    }
}
