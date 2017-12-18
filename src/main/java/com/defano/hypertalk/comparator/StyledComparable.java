package com.defano.hypertalk.comparator;

import com.defano.hypertalk.ast.model.SortStyle;

public interface StyledComparable<T> extends Comparable<T> {

    int compareTo(T to, SortStyle style);

}
