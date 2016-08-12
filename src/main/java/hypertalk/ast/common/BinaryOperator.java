/**
 * BinaryOperator.java
 * @author matt.defano@gmail.com
 * 
 * Enumeration of supported binary operators
 */

package hypertalk.ast.common;

public enum BinaryOperator {
    EQUALS, NOTEQUALS, LESSTHAN, GREATERTHAN, LESSTHANOREQUALS, GREATERTHANOREQUALS,
    PLUS, MINUS, MULTIPLY, DIVIDE, MOD, EXP,
    AND, OR, CONTAINS, NOTCONTAINS, CONCAT
}
