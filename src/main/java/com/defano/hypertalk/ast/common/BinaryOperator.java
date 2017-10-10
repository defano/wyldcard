package com.defano.hypertalk.ast.common;

import java.util.Arrays;
import java.util.List;

public enum BinaryOperator {
    EQUALS("=", "is"),
    NOT_EQUALS("<>", "is not", "≠"),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_OR_EQUALS("<=", "≤"),
    GREATER_THAN_OR_EQUALS(">=", "≥"),
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MOD("mod"),
    EXP("^"),
    AND("and"),
    OR("or"),
    CONTAINS("contains"),
    IS_IN("is in"),
    IS_NOT_IN("is not in"),
    AMP("&"),
    AMP_AMP("&&"),
    IS_WITHIN("is within"),
    IS_NOT_WITHIN("is not within"),
    IS_A("is a", "is an"),
    IS_NOT_A("is not a", "is not an");

    private final List<String> tokens;

    BinaryOperator(String... operatorTokens) {
        tokens = Arrays.asList(operatorTokens);
    }

    public static BinaryOperator fromName (String name) {
        for (BinaryOperator thisOperator : values()) {
            if (thisOperator.tokens.contains(name.toLowerCase())) {
                return thisOperator;
            }
        }

        throw new RuntimeException("Bug! No such binary operator " + name);
    }
}
