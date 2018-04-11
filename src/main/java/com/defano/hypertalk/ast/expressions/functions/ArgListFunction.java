package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public abstract class ArgListFunction extends Expression {

    private final Expression arguments;

    public ArgListFunction(ParserRuleContext context, Expression arguments) {
        super(context);
        this.arguments = arguments;
    }

    /**
     * Evaluates this list of argument expressions and returns a list of values.
     *
     * When constructed with a single argument Expression, this method returns a single-item list containing the
     * evaluated result of the single argument expression.
     *
     * When constructed with an ExpressionList, this method performing a "diving evaluation" of each expression in
     * the ExpressionList and returns a list of Values the size of which is equal to the number of items appearing
     * in the ExpressionList. Note that this is not necessarily the same as the length of the ExpressionList itself;
     * this method attempts to pull apart any single argument into a sublist of arguments.
     *
     * For example, (1, 2, 3) results in three values '1', '2', '3'); and so does (1, "2, 3").
     *
     * @param context The execution context
     * @return A list of evaluated arguments passed to the arg list function.
     * @throws HtSemanticException If an error occurs evaluating the expressions.
     */
    public List<Value> evaluateArgumentList(ExecutionContext context) throws HtException {
        return arguments.evaluateAsList(context);
    }

    /**
     * Assumes the argument list contains only a single argument and returns the evaluation of it; produces a semantic
     * error if the number of arguments is not 1.
     *
     * @param context The execution context
     * @return The evaluated singleton argument value.
     * @throws HtSemanticException If an error occurs evaluating the expressions.
     */
    public Value evaluateSingleArgumentList(ExecutionContext context) throws HtException {
        return arguments.evaluate(context);
    }

}
