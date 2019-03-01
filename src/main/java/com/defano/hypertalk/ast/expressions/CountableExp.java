package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.model.Countable;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * A special type of non-evaluable expression that refers to an item that can be counted (that is, something that can
 * be passed as an argument to the number function).
 */
public class CountableExp extends Expression {

    private final Countable countable;      // The type of thing being counted
    private final Expression argument;      // Optional argument referring to the container of the items being counted

    public CountableExp(ParserRuleContext context, Countable countable, Expression argument) {
        super(context);
        this.countable = countable;
        this.argument = argument;
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        // This is a syntax placeholder expression; it cannot be legally evaluated on its own
        throw new HtSemanticException("Don't understand that.");
    }

    public Countable getCountable() {
        return countable;
    }

    public Expression getArgument() {
        return argument;
    }
}
