package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.runtime.manager.SelectionManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectedFieldFunc extends Expression {

    @Inject
    private SelectionManager selectionManager;

    public SelectedFieldFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        try {
            return selectionManager.getManagedSelection(context).getSelectedFieldExpression(context);
        } catch (HtSemanticException e) {
            return new Value();
        }
    }
}
