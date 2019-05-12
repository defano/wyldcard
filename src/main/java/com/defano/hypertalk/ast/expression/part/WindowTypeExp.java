package com.defano.hypertalk.ast.expression.part;

import com.defano.hypertalk.ast.expression.container.PartExp;
import com.defano.hypertalk.ast.model.enums.SingletonWindowType;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.ast.model.specifier.WindowTypeSpecifier;
import com.defano.wyldcard.runtime.ExecutionContext;
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
