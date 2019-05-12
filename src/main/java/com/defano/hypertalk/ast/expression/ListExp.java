package com.defano.hypertalk.ast.expression;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.common.collect.Lists;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

/**
 * An expression composed of a list of other expressions as seen in argument lists.
 */
public class ListExp extends Expression {

    private final Expression car;   // First item in list
    private final ListExp cdr;      // Remaining items in list

    /**
     * Constructs an empty list expression.
     *
     * @param ctx The Antlr context where this expression was encountered, or null
     */
    public ListExp(ParserRuleContext ctx) {
        this(ctx, new LiteralExp(ctx));
    }

    /**
     * Constructs a singleton list expression.
     *
     * @param ctx The Antlr context where this expression was encountered, or null
     * @param car The single expression making up this list
     */
    public ListExp(ParserRuleContext ctx, Expression car) {
        this(ctx, car, null);
    }

    /**
     * Constructs a list expression containing multiple expressions.
     *
     * @param ctx The Antlr context where this expression was encountered, or null
     * @param car The first expression in the list
     * @param cdr A list of subsequent expressions
     */
    public ListExp(ParserRuleContext ctx, Expression car, ListExp cdr) {
        super(ctx);
        this.car = car;
        this.cdr = cdr;
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        if (cdr != null) {
            return new Value(car.evaluate(context).toString() + "," + cdr.evaluate(context).toString());
        } else {
            return car.evaluate(context);
        }
    }

    /**
     * Gets the non-null first expression in this list.
     *
     * @return The first expression in the list.
     */
    @SuppressWarnings("unused")
    public Expression car() {
        return car;
    }

    /**
     * Gets a {@link ListExp} representing all but the first expression in the list.
     *
     * @return Null if there is only only expression in the list, otherwise, all but the first element in the list.
     */
    public ListExp cdr() {
        return cdr;
    }

    /**
     * Evaluates the cdr as a flattened list of evaluated values.
     *
     * @param context The execution context
     * @return The evaluated list of values contained in the cdr, or an empty list if this ListExp contains only a
     * single expression.
     * @throws HtException Thrown if an error occurs while evaluating the list.
     */
    public List<Value> evaluateCdrAsList(ExecutionContext context) throws HtException {
        return cdr == null ? new ArrayList<>() : cdr.evaluateAsList(context);
    }

    /**
     * Evaluates the car as a list expression by evaluating the expression then interpreting it as a comma-delimited,
     * HyperTalk list.
     *
     * @param context The execution context
     * @return A list of comma-seperated values produced by evaluating this list expression's car
     * @throws HtException Thrown if an error occurs while evaluating the list.
     */
    public List<Value> evaluateCarAsList(ExecutionContext context) throws HtException {
        return car.evaluateAsList(context);
    }

    /**
     * Evaluates this expression as a list of values. The size of the list of values returned is equal to the number
     * of expressions contained within this object.
     * <p>
     * If this {@link ListExp} is a singleton list (has a car, but no cdr), then a single value equal to evaluating the
     * car is returned. If this {@link ListExp} has a non-null cdr, then the car is evaluated and the cdr is recursively
     * evaluated.
     *
     * @param context The execution context
     * @return A list of values created by recursively evaluating the car and cdr portions of this list expression.
     * @throws HtException Thrown if an error occurs while evaluating the expression.
     */
    @Override
    public List<Value> evaluateAsList(ExecutionContext context) throws HtException {
        if (cdr != null) {
            List<Value> values = Lists.newArrayList(car.evaluate(context));
            values.addAll(evaluateCdrAsList(context));
            return values;
        } else {
            return Lists.newArrayList(car.evaluate(context));
        }
    }

    public List<Value> divingSingletonEvaluation(ExecutionContext context) throws HtException {
        if (cdr != null) {
            return evaluateAsList(context);
        } else {
            return car.evaluate(context).getListItems();
        }
    }

}
