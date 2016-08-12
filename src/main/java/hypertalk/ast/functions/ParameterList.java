/**
 * ParameterList.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a user-defined function's parameter list
 */

package hypertalk.ast.functions;

import java.util.List;
import java.util.Vector;

public class ParameterList {

    public List<String> list;
    
    public ParameterList () {
        list = new Vector<>();
    }
    
    public ParameterList(String p) {
        list = new Vector<>();
        list.add(p);
    }
    
    public ParameterList addParameter (String p) {
        list.add(p);
        return this;
    }
}
