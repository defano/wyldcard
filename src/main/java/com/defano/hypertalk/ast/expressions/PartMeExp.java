package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartMeExp extends PartContainerExp {

    public PartMeExp(ParserRuleContext context) {
        super(context);
    }

    public Value onEvaluate() throws HtSemanticException {
        try {
            PartSpecifier part = ExecutionContext.getContext().getMe();
            return ExecutionContext.getContext().getPart(part).getValue();
        } catch (PartException e) {
            throw new HtSemanticException(e.getMessage());
        }
    }
    
    public PartSpecifier evaluateAsSpecifier () 
    throws HtSemanticException
    {        
        return ExecutionContext.getContext().getMe();
    }    
}
