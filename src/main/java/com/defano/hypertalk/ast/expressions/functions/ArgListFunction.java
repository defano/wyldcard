package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.ListExp;
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
     * Evaluates an expression representing a function's argument list as a list of WyldCard values.
     * <p>
     * If the expression is of type {@link ListExp}, then the size of the list returned is equal to the number of
     * expressions in the {@link ListExp}.
     * <p>
     * If the expression is not a {@link ListExp}, or if the {@link ListExp} contains only one expression, then the
     * expression is evaluated and the result is interpreted as a comma-separated list and the list of comma-separated
     * values is returned.
     * <p>
     * For example, the single expression '"1,2,3"' evaluates to a list of three numeric values; so does the
     * {@link ListExp} '1,2,3'. The list expression '1, "2, 3"' however, evaluates to the number value '1' and the
     * string value "2, 3".
     *
     * @param context The execution context
     * @return A list of evaluated arguments passed to the arg list function.
     * @throws HtSemanticException If an error occurs evaluating the expressions.
     */
    public List<Value> evaluateArgumentList(ExecutionContext context) throws HtException {
        if (arguments instanceof ListExp) {
            if (((ListExp) arguments).cdr() == null) {
                return ((ListExp) arguments).evaluateCarAsList(context);
            } else {
                return arguments.evaluateAsList(context);
            }
        }

        else {
            List<Value> evaluated = arguments.evaluateAsList(context);
            if (evaluated.size() == 1 && evaluated.get(0).isQuotedLiteral()) {
                return evaluated.get(0).getListItems();
            }
            return evaluated;
        }
    }

    /**
     * Gets the expression representing the argument/s passed to this function.
     * @return The arguments passed to this function.
     */
    public Expression getArguments() {
        return arguments;
    }
}
