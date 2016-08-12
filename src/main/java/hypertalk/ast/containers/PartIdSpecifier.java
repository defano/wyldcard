/**
 * PartIdSpecifier.java
 * @author matt.defano@gmail.com
 * 
 * ID-based specification of a part, for example "field id 22"
 */

package hypertalk.ast.containers;

import hypertalk.ast.common.PartType;


public class PartIdSpecifier implements PartSpecifier {

    public PartType type;
    public int id;

    public PartIdSpecifier() {}

    public PartIdSpecifier(PartType type, int id) {
        this.type = type;
        this.id = id;
    }
    
    public PartType type () {
        return type;
    }
    
    public Integer value () {
        return id;
    }
    
    public String toString () {
        return type + " id " + id;
    }
}
