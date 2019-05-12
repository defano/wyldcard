package com.defano.hypertalk.ast.expression.operator;

import java.util.Arrays;
import java.util.List;

public enum BinaryOperator {
    EQUALS("=", "is"),
    NOT_EQUALS("<>", "isnot", "≠"),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_OR_EQUALS("<=", "≤"),
    GREATER_THAN_OR_EQUALS(">=", "≥"),
    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIV("div"),
    DIVIDE("/"),
    MOD("mod"),
    EXP("^"),
    AND("and"),
    OR("or"),
    CONTAINS("contains"),
    IS_IN("isin"),
    IS_NOT_IN("isnotin"),
    AMP("&"),
    AMP_AMP("&&"),
    IS_WITHIN("iswithin"),
    IS_NOT_WITHIN("isnotwithin"),
    IS_A("isa", "isan"),
    IS_NOT_A("isnota", "isnotan");

    private final List<String> tokens;

    BinaryOperator(String... operatorTokens) {
        tokens = Arrays.asList(operatorTokens);
    }

    public static BinaryOperator fromName (String name) {
        name = name.replace(" ", "");

        for (BinaryOperator thisOperator : values()) {
            if (thisOperator.tokens.contains(name.toLowerCase())) {
                return thisOperator;
            }
        }

        throw new RuntimeException("Bug! No such binary operator " + name);
    }
}
