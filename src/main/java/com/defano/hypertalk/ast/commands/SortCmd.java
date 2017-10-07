/*
 * StatSortCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.common.SortDirection;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.comparator.ExpressionValueComparator;
import com.defano.hypertalk.comparator.SortStyle;
import com.defano.hypertalk.comparator.ValueComparator;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.ChunkType;
import com.defano.hypertalk.ast.containers.Container;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class SortCmd extends Command {

    public final SortDirection direction;
    public final ChunkType chunkType;
    public final Container container;
    public final Expression expression;
    public final SortStyle sortStyle;

    public SortCmd(ParserRuleContext context, Container container, ChunkType chunkType, Expression expression, SortDirection direction, SortStyle sortStyle) {
        super(context, "sort");

        this.container = container;
        this.chunkType = chunkType;
        this.expression = expression;
        this.direction = direction;
        this.sortStyle = sortStyle;
    }

    public SortCmd(ParserRuleContext context, Container container, ChunkType chunkType, SortDirection direction, SortStyle sortStyle) {
        super(context, "sort");

        this.container = container;
        this.chunkType = chunkType;
        this.direction = direction;
        this.expression = null;
        this.sortStyle = sortStyle;
    }

    public void onExecute() throws HtException {
        List<Value> items = getItemsToSort();

        // Sort by direction
        if (expression == null) {
            items.sort(new ValueComparator(direction, sortStyle));
        }

        // Sort by expression
        else {
            items.sort(new ExpressionValueComparator(expression, direction, sortStyle));
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
            throw new HtSemanticException("Cannot sort by '" + chunkType + "'. Only 'lines' or 'items' are supported.");
        }

        if (chunkType == ChunkType.LINE) {
            return container.getValue().getLines();
        } else {
            return container.getValue().getItems();
        }
    }

}
