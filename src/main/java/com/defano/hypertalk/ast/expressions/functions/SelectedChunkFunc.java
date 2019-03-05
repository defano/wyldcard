package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.SelectionManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectedChunkFunc extends Expression {

    @Inject
    private SelectionManager selectionManager;

    public SelectedChunkFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) {
        try {
            return selectionManager.getManagedSelection(context).getSelectedChunkExpression(context);
        } catch (HtSemanticException e) {
            return new Value();
        }
    }
}
