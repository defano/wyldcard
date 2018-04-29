package com.defano.hypertalk.ast.expressions.parts;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.SingletonWindowType;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.WindowTypeSpecifier;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class WindowTypeExp extends PartExp {

    private final SingletonWindowType type;

    public WindowTypeExp(ParserRuleContext context, SingletonWindowType type) {
        super(context);
        this.type = type;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return new WindowTypeSpecifier(context, type);
    }
}
