/*
 * ExpPartName
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpPartName.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of name-based part specification, for example: "field myField"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.containers.PartNameSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpPartName extends ExpPart {

    public final PartType type;
    public final Expression name;
    
    public ExpPartName (PartType type, Expression name) {
        this.type = type;
        this.name = name;
    }
    
    public Value evaluate () throws HtSemanticException {
        try {
            PartSpecifier part = new PartNameSpecifier(type, name.evaluate().stringValue());
            return GlobalContext.getContext().get(part).getValue();
        } catch (Exception e) {
            throw new HtSemanticException(e.getMessage());
        }
    }
    
    public PartSpecifier evaluateAsSpecifier () 
    throws HtSemanticException
    {        
        return new PartNameSpecifier(type, name.evaluate().stringValue());
    }    
}
