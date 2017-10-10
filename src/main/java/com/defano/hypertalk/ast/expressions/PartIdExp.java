package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartIdExp extends PartExp {

    public final Owner layer;
    public final PartType type;
    public final Expression id;

    public PartIdExp(ParserRuleContext context, PartType type, Expression id) {
        this(context, null, type, id);
    }

    public PartIdExp(ParserRuleContext context, Owner layer, PartType type, Expression id) {
        super(context);
        this.layer = layer;
        this.type = type;
        this.id = id;
    }
    
    public Value onEvaluate() throws HtException {
        try {
            return ExecutionContext.getContext().get(evaluateAsSpecifier()).getValue();
        } catch (PartException e) {
            throw new HtSemanticException("Can't get that part.");
        }
    }
    
    public PartSpecifier evaluateAsSpecifier () throws HtException
    {        
        return new PartIdSpecifier(layer, type, id.evaluate().integerValue());
    }
    
}
