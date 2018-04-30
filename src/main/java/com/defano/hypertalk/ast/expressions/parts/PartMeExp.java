package com.defano.hypertalk.ast.expressions.parts;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartMeExp extends PartExp {

    public PartMeExp(ParserRuleContext context) {
        super(context);
    }

    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return context.getStackFrame().getMe();
    }
}
