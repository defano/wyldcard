/*
 * Destination
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.common;

import com.defano.hypertalk.ast.containers.*;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class Destination {

    public final DestinationType destinationType;

    public final Expression expression;
    public final Position position;
    public final Ordinal ordinal;

    public Destination(Ordinal ordinal, DestinationType type) {
        this.destinationType = type;
        this.ordinal = ordinal;
        this.expression = null;
        this.position = null;
    }

    public Destination(Position position, DestinationType type) {
        this.destinationType = type;
        this.position = position;
        this.expression = null;
        this.ordinal = null;
    }

    public Destination(Expression number, DestinationType type) {
        this.destinationType = type;
        this.expression = number;
        this.position = null;
        this.ordinal = null;
    }

    public PartSpecifier evaluateAsPartSpecifier() throws HtSemanticException {
       if (position != null) {
           return new PartPositionSpecifier(Owner.STACK, destinationType.asPartType(), position);
       } else if (ordinal != null) {
           return new PartOrdinalSpecifier(Owner.STACK, destinationType.asPartType(), ordinal);
       } else if (expression != null) {
           Value evaluatedName = expression.evaluate();

           if (evaluatedName.isInteger()) {
               return new PartNumberSpecifier(Owner.STACK, destinationType.asPartType(), evaluatedName.integerValue());
           } else {
               return new PartNameSpecifier(Owner.STACK, destinationType.asPartType(), evaluatedName.stringValue());
           }
       }

       throw new IllegalStateException("Bug! Invalid destination.");
    }
}
