/**
 * Script.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a HyperTalk script; might include user defined functions,
 * handlers, or loose statements.
 */

package hypertalk.ast.common;

import hypertalk.ast.functions.UserFunction;
import hypertalk.ast.statements.StatementList;

import java.util.HashMap;
import java.util.Map;

public class Script {

    private Map<String, StatementList> handlers;
    private Map<String, UserFunction> functions;
    private StatementList statements = null;
    
    public Script () {
        handlers = new HashMap<>();
        functions = new HashMap<>();
    }

    public Script defineHandler (NamedBlock handler) {
        handlers.put(handler.name.toLowerCase(), handler.body);
        return this;
    }
    
    public Script defineUserFunction (UserFunction function) {
        functions.put(function.name.toLowerCase(), function);
        return this;
    }
    
    public Script defineStatementList (StatementList statements) {
        this.statements = statements;
        return this;
    }

    public StatementList getHandler(String handler) {
        return handlers.get(handler.toLowerCase());
    }

    public UserFunction getFunction(String function) {
        return functions.get(function.toLowerCase());
    }

    public StatementList getStatements() {
        return statements;
    }
}
