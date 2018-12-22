package com.defano.hypertalk.ast.expressions.operators;

import java.util.Arrays;
import java.util.List;

public enum UnaryOperator {
    NOT("not"),
    NEGATE("-"),
    THERE_IS_A("thereisa", "thereisan"),
    THERE_IS_NOT_A("thereisno", "thereisnota", "thereisnotan");

    private final List<String> tokens;

    UnaryOperator(String... operatorTokens) {
        tokens = Arrays.asList(operatorTokens);
    }

    public static UnaryOperator fromName (String name) {
        name = name.replace(" ", "");

        for (UnaryOperator thisOperator : values()) {
            if (thisOperator.tokens.contains(name.toLowerCase())) {
                return thisOperator;
            }
        }

        throw new RuntimeException("Bug! No such binary operator " + name);
    }

}
