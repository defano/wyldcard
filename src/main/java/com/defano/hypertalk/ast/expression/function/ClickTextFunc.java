package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.runtime.manager.SelectionManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class ClickTextFunc extends Expression {

    @Inject
    private SelectionManager selectionManager;

    public ClickTextFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) {
        return selectionManager.getClickText();
    }
}
