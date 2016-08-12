/**
 * Frame.java
 * @author matt.defano@gmail.com
 * 
 * Maintains the current local context; analagous to the current stack frame.
 */

package hypercard.context;

import hypertalk.ast.common.Value;

import java.util.List;

public class Frame {

    public final SymbolTable symbols;
    private final List<String> globalsInScope;
    
    private Value returnValue;
    
    public Frame(SymbolTable symbols, List<String> globalsInScope, Value returnValue) {
        this.symbols = symbols;
        this.globalsInScope = globalsInScope;
        this.returnValue = returnValue;
        
        // "it" is implemented as a global variable that's always in scope
        globalsInScope.add("it");
    }
    
    public void globalInScope (String symbol) {
        globalsInScope.add(symbol);
    }
    
    public boolean isGlobalInScope (String symbol) {
        return globalsInScope.contains(symbol);
    }
    
    public void setReturnValue (Value returnValue) {
        this.returnValue = returnValue;
    }
    
    public Value getReturnValue () {
        return returnValue;
    }
}
