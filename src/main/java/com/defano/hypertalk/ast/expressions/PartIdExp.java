/*
 * ExpPartId
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:12 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PartIdExp.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of an id-based part specification, for example: "button id 12"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class PartIdExp extends PartExp {

    public final Owner layer;
    public final PartType type;
    public final Expression id;

    public PartIdExp(PartType type, Expression id) {
        this(null, type, id);
    }

    public PartIdExp(Owner layer, PartType type, Expression id) {
        this.layer = layer;
        this.type = type;
        this.id = id;
    }
    
    public Value evaluate () throws HtSemanticException {
        try {
            return ExecutionContext.getContext().get(evaluateAsSpecifier()).getValue();
        } catch (PartException e) {
            throw new HtSemanticException("Can't get that part.");
        }
    }
    
    public PartSpecifier evaluateAsSpecifier () 
    throws HtSemanticException
    {        
        return new PartIdSpecifier(layer, type, id.evaluate().integerValue());
    }
    
}
