package com.defano.hypertalk.ast.model;

import com.defano.hypertalk.ast.model.enums.ConvertibleDateFormat;

public class Convertible {

    public final ConvertibleDateFormat first;
    public final ConvertibleDateFormat second;

    public Convertible(ConvertibleDateFormat first) {
        this(first, null);
    }

    public Convertible(ConvertibleDateFormat first, ConvertibleDateFormat second) {
        this.first = first;
        this.second = second;
    }

}
