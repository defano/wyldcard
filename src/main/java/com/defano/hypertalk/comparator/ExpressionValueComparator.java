package com.defano.hypertalk.comparator;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.SortDirection;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;

import java.util.Comparator;

public class ExpressionValueComparator implements Comparator<Value> {

    private final Expression expression;
    private final SortStyle sortStyle;
    private final SortDirection sortDirection;

    public ExpressionValueComparator(Expression expression, SortDirection sortDirection, SortStyle sortStyle) {
        this.expression = expression;
        this.sortStyle = sortStyle;
        this.sortDirection = sortDirection;
    }

    @Override
    public int compare(Value o1, Value o2) {
        try {
            ExecutionContext.getContext().setVariable("each", o1);
            Value o1Evaluated = expression.evaluate();

            ExecutionContext.getContext().setVariable("each", o2);
            Value o2Evaluated = expression.evaluate();

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
