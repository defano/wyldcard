package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.SingletonWindowType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.WindowNameSpecifier;
import com.defano.hypertalk.ast.model.specifiers.WindowNumberSpecifier;
import com.defano.hypertalk.ast.model.specifiers.WindowTypeSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.HyperCardWindow;
import com.defano.wyldcard.window.WindowManager;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class WindowTypeExp extends PartExp {

    private final SingletonWindowType type;

    public WindowTypeExp(ParserRuleContext context, SingletonWindowType type) {
        super(context);
        this.type = type;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) {
        return new WindowTypeSpecifier(type);
    }
}
