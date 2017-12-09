package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartIdExp extends PartContainerExp {

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
    
    public PartSpecifier evaluateAsSpecifier () throws HtException
    {        
        return new PartIdSpecifier(layer, type, id.evaluate().integerValue());
    }
    
}
