/*
 * ExpPartMe
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:12 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PartMeExp.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a part self-reference, for example: "me"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class PartMeExp extends PartExp {

    public PartMeExp() {}
    
    public Value evaluate () throws HtSemanticException {
        try {
            PartSpecifier part = GlobalContext.getContext().getMe();
            return GlobalContext.getContext().get(part).getValue();
        } catch (PartException e) {
            throw new HtSemanticException(e.getMessage());
        }
    }
    
    public PartSpecifier evaluateAsSpecifier () 
    throws HtSemanticException
    {        
        return GlobalContext.getContext().getMe();
    }    
}
