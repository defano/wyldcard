/**
 * Preposition.java
 * @author matt.defano@gmail.com
 * 
 * Enumeration of acceptable prepositions
 */

package hypertalk.ast.containers;

public enum Preposition {
    BEFORE, AFTER, INTO;

    public static Preposition fromString(String s) {
        switch (s) {
            case "before": return BEFORE;
            case "after": return AFTER;
            case "into": return INTO;
            default: throw new IllegalArgumentException("Bug! Unimplemented preposition.");
        }
    }
}
