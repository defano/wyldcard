package com.defano.hypertalk.ast.expressions.operators;

import java.util.Arrays;
import java.util.List;

public enum UnaryOperator {
    NOT("not"),
    NEGATE("-"),
    THERE_IS_A("there is a", "there is an"),
    THERE_IS_NOT_A("there is no", "there is not a", "there is not an");

    private final List<String> tokens;

    UnaryOperator(String... operatorTokens) {
        tokens = Arrays.asList(operatorTokens);
    }

    public static UnaryOperator fromName (String name) {
        for (UnaryOperator thisOperator : values()) {
            if (thisOperator.tokens.contains(name.toLowerCase())) {
                return thisOperator;
            }
        }

        throw new RuntimeException("Bug! No such binary operator " + name);
    }

}
