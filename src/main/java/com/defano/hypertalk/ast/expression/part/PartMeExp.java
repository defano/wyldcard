package com.defano.hypertalk.ast.expression.part;

import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartMeExp extends PartExp {

    public PartMeExp(ParserRuleContext context) {
        super(context);
    }

    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return context.getStackFrame().getMe();
    }
}
