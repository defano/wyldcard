package com.defano.hypertalk.comparator;

import com.defano.hypertalk.ast.common.SortDirection;
import com.defano.hypertalk.ast.common.Value;

import java.util.Comparator;

public class ValueComparator implements Comparator<Value> {
    private final SortDirection direction;

    public ValueComparator(SortDirection direction) {
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
