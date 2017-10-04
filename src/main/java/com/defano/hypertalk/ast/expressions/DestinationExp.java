/*
 * Destination
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

public abstract class DestinationExp extends PartExp {

    @Override
    public Value evaluate() throws HtSemanticException {
        throw new IllegalStateException("Destinations are not evaluable.");
    }
}
