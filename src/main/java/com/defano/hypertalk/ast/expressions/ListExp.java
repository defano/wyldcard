package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An expression composed of a list of other expressions seen in argument lists.
 */
public class ListExp extends Expression {

    private final Expression car;   // First item in list
    private final ListExp cdr;      // Remaining items in list

    /**
     * Constructs an empty list expression.
     * @param ctx The Antlr context where this expression was encountered, or null
     */
    public ListExp(ParserRuleContext ctx) {
        this(ctx, new LiteralExp(ctx));
    }

    /**
     * Constructs a singleton list expression.
     * @param ctx The Antlr context where this expression was encountered, or null
     * @param car The single expression making up this list
     */
    public ListExp(ParserRuleContext ctx, Expression car) {
        this(ctx, car, null);
    }

    /**
     * Constructs a list expression containing multiple expressions.
     * @param ctx The Antlr context where this expression was encountered, or null
     * @param car The first expression in the list
     * @param cdr A list of subsequent expressions
     */
    public ListExp(ParserRuleContext ctx, Expression car, ListExp cdr) {
        super(ctx);
        this.car = car;
        this.cdr = cdr;
    }

    /**
     * Constructs a list expression where each element in the list is a literal expression of a given value.
     * @param ctx The Antlr context where this expression was encountered, or null
     * @param values The list of literal values to be added to the list
     * @return A ListExp representing the given values
     */
    public static ListExp fromValues(ParserRuleContext ctx, Value... values) {
        if (values == null || values.length == 0) {
            return new ListExp(ctx);
        } else if (values.length == 1) {
            return new ListExp(ctx, new LiteralExp(ctx, values[0]));
        } else {
            return new ListExp(ctx, new LiteralExp(ctx, values[0]), ListExp.fromValues(ctx, Arrays.copyOfRange(values, 1, values.length)));
        }
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        if (cdr != null) {
            return new Value(car.evaluate(context).toString() + "," + cdr.evaluate(context).toString());
        } else {
            return car.evaluate(context);
        }
    }

    @Override
    public List<Value> evaluateAsList(ExecutionContext context) throws HtException {
        ArrayList<Value> values = new ArrayList<>(car.evaluateAsList(context));

        if (cdr != null) {
            values.addAll(cdr.evaluateAsList(context));
        }

        return values;
    }

}
