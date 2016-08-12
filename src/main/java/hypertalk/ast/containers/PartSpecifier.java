/**
 * PartSpecifier.java
 * @author matt.defano@gmail.com
 * 
 * Abstract superclass for part specifiers
 */

package hypertalk.ast.containers;

import hypertalk.ast.common.PartType;


public interface PartSpecifier {
    Object value();
    PartType type();
    String toString();
}
