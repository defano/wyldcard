package com.defano.hypertalk.comparator;

import com.defano.hypertalk.ast.model.SortStyle;

public interface StyledComparable<T> extends Comparable<T> {

    /**
     * Performs a comparison of two values, using HyperTalk sort semantics. That is, this comparator attempts to coerce
     * each value into a given type and compare them using sematics appropriate for the type (i.e., lexical, numeric,
     * date-time, etc.)
     *
     * @param to The value to which the current value should be compared
     * @param style The "style" or sort strategy to apply
     * @return The value {@code 0} if the argument value is equal to this value; a value less than {@code 0} if this
     * value is less than the value argument; and a value greater than {@code 0} if this value is greater than the value
     * argument
     */
    int compareTo(T to, SortStyle style);

}
