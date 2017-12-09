package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartMeExp extends PartContainerExp {

    public PartMeExp(ParserRuleContext context) {
        super(context);
    }

    public PartSpecifier evaluateAsSpecifier ()
    throws HtSemanticException
    {        
        return ExecutionContext.getContext().getMe();
    }    
}
