/**
 * PartNameSpecifier.java
 * @author matt.defano@gmail.com
 * 
 * Name-based specification of a part, for example, "button myButton"
 */

package hypertalk.ast.containers;

import hypertalk.ast.common.PartType;


public class PartNameSpecifier implements PartSpecifier {

    public PartType type;
    public String name;

    public PartNameSpecifier () {}

    public PartNameSpecifier (PartType type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public PartType type() {
        return type;
    }
    
    public String value() {
        return name;
    }
    
    public String toString() {
        return type + " " + name;
    }
}
