/**
 * BinaryOperator.java
 * @author matt.defano@gmail.com
 * 
 * Enumeration of supported binary operators
 */

package hypertalk.ast.common;

import java.util.Arrays;
import java.util.List;

public enum BinaryOperator {
    EQUALS("=", "is"),
    NOTEQUALS("<>", "is not"),
    LESSTHAN("<"),
    GREATERTHAN(">"),
    LESSTHANOREQUALS("<="),
    GREATERTHANOREQUALS(">="),
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
    CONCAT("&", "&&");

    private List<String> tokens;

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
