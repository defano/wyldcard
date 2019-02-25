package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.SelectionManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class ClickTextFunc extends Expression {

    @Inject
    private SelectionManager selectionManager;

    public ClickTextFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        return selectionManager.getClickText();
    }
}
