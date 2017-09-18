package com.defano.hypertalk.comparator;

import com.defano.hypertalk.ast.common.SortDirection;
import com.defano.hypertalk.ast.common.Value;

import java.util.Comparator;

public class ValueComparator implements Comparator<Value> {
    private final SortDirection direction;
    private final SortStyle sortStyle;

    public ValueComparator(SortDirection direction, SortStyle sortStyle) {
        this.direction = direction;
        this.sortStyle = sortStyle;
    }

    @Override
    public int compare(Value o1, Value o2) {
        if (direction == SortDirection.ASCENDING) {
            return o1.compareTo(o2, sortStyle);
        } else {
            return o2.compareTo(o1, sortStyle);
        }
    }
}
