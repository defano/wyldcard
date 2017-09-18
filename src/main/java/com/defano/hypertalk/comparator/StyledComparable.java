package com.defano.hypertalk.comparator;

public interface StyledComparable<T> extends Comparable<T> {

    int compareTo(T to, SortStyle style);

}
