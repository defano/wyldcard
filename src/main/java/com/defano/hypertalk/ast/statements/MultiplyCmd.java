/*
 * StatMultiplyCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.containers.Preposition;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.containers.Container;

public class MultiplyCmd extends Command {

    private final Expression expression;
    private final Container container;

    public MultiplyCmd(Expression source, Container container) {
        super("multiply");

        this.expression = source;
        this.container = container;
    }

    public void onExecute() throws HtException {
        container.putValue(container.getValue().multiply(expression.evaluate()), Preposition.INTO);
    }
}
