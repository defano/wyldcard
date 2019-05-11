package com.defano.hypertalk.comparator;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.model.enums.SortDirection;
import com.defano.hypertalk.ast.model.enums.SortStyle;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;

import java.util.Comparator;

public class ExpressionValueComparator implements Comparator<Value> {

    private final ExecutionContext context;
    private final Expression expression;
    private final SortStyle sortStyle;
    private final SortDirection sortDirection;

    public ExpressionValueComparator(ExecutionContext context, Expression expression, SortDirection sortDirection, SortStyle sortStyle) {
        this.context = context;
        this.expression = expression;
        this.sortStyle = sortStyle;
        this.sortDirection = sortDirection;
    }

    @Override
    public int compare(Value o1, Value o2) {
        try {
            context.setVariable("each", o1);
            Value o1Evaluated = expression.evaluate(context);

            context.setVariable("each", o2);
            Value o2Evaluated = expression.evaluate(context);

            if (sortDirection == SortDirection.ASCENDING) {
                return o1Evaluated.compareTo(o2Evaluated, sortStyle);
            } else {
                return o2Evaluated.compareTo(o1Evaluated, sortStyle);
            }

        } catch (HtException e) {
            throw new HtUncheckedSemanticException(e);
        }
    }
}
