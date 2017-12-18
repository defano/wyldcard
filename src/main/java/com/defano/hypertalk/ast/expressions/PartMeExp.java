package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartMeExp extends PartExp {

    public PartMeExp(ParserRuleContext context) {
        super(context);
    }

    public PartSpecifier evaluateAsSpecifier ()
    throws HtSemanticException
    {        
        return ExecutionContext.getContext().getMe();
    }    
}
