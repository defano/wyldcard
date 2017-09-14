/*
 * StatSortCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.SortDirection;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.Preposition;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.ChunkType;
import com.defano.hypertalk.ast.containers.Container;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.Comparator;
import java.util.List;

public class SortCmd extends Command {

    public final SortDirection direction;
    public final ChunkType chunkType;
    public final Container container;
    public final Expression expression;

    public SortCmd(Container container, ChunkType chunkType, Expression expression) {
        super("sort");

        this.container = container;
        this.chunkType = chunkType;
        this.expression = expression;
        this.direction = null;
    }

    public SortCmd(Container container, ChunkType chunkType, SortDirection direction) {
        super("sort");

        this.container = container;
        this.chunkType = chunkType;
        this.direction = direction;
        this.expression = null;
    }

    public void onExecute() throws HtException {
        List<Value> items = getItemsToSort();

        // Sort by direction
        if (expression == null) {
            items.sort(new ValueComparator(direction));
        }

        // Sort by expression
        else {
            items.sort(new ExpressionValueComparator(expression));
        }

        putSortedItems(items);
    }

    private void putSortedItems(List<Value> sortedItems) throws HtException {
        if (chunkType == ChunkType.LINE) {
            container.putValue(Value.ofLines(sortedItems), Preposition.INTO);
        } else {
            container.putValue(Value.ofItems(sortedItems), Preposition.INTO);
        }
    }

    private List<Value> getItemsToSort() throws HtException {
        if (chunkType != ChunkType.LINE && chunkType != ChunkType.ITEM) {
            throw new HtSemanticException("Cannot sort by " + chunkType + ". Only 'lines' or 'items' are supported.");
        }

        if (chunkType == ChunkType.LINE) {
            return container.getValue().getLines();
        } else {
            return container.getValue().getItems();
        }
    }

    private static class ExpressionValueComparator implements Comparator<Value> {

        private final Expression expression;

        private ExpressionValueComparator(Expression expression) {
            this.expression = expression;
        }

        @Override
        public int compare(Value o1, Value o2) {
            try {
                ExecutionContext.getContext().set("each", o1);
                Value o1Evaluated = expression.evaluate();

                ExecutionContext.getContext().set("each", o2);
                Value o2Evaluated = expression.evaluate();

                return o1Evaluated.compareTo(o2Evaluated);
            } catch (HtSemanticException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ValueComparator implements Comparator<Value> {
        private final SortDirection direction;

        private ValueComparator(SortDirection direction) {
            this.direction = direction;
        }

        @Override
        public int compare(Value o1, Value o2) {
            if (direction == SortDirection.ASCENDING) {
                return o1.compareTo(o2);
            } else {
                return o2.compareTo(o1);
            }
        }
    }

}
