package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.search.SearchManager;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class FoundTextFunc extends Expression {

    @Inject
    private SearchManager searchManager;

    public FoundTextFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) {
        return searchManager.getFoundText();
    }
}
