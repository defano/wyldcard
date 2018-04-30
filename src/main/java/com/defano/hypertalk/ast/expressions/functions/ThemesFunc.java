package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;
import org.antlr.v4.runtime.ParserRuleContext;

public class ThemesFunc extends Expression {

    public ThemesFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return Value.ofItems(WindowManager.getInstance().getThemeNames());
    }
}
