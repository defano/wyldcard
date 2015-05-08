/**
 * Interpreter.java
 * @author matt.defano@gmail.com
 * 
 * Interpreter class provides static methods for compiling a script (translate
 * into an AST) as well as executing a string as a script.
 */

package hypercard.runtime;

import hypertalk.ast.common.Script;
import hypertalk.exception.HtSyntaxException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.parser.HtInterpreter;
import hypertalk.parser.HtLexer;

import java.io.ByteArrayInputStream;

import java.io.Serializable;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;

public class Interpreter implements Serializable {
private static final long serialVersionUID = 1193147567809491083L;

	public static Script compile (String scriptText) throws HtSyntaxException {
		
		scriptText = canonicalScriptForm(scriptText);
		
		try {
            SymbolFactory sf = new ComplexSymbolFactory();
        	HtLexer lexer = new HtLexer(new ByteArrayInputStream(scriptText.getBytes()), sf);		
    		HtInterpreter parser = new HtInterpreter(lexer, sf);	
            
			Symbol ast = parser.parse();
			return (Script) ast.value;		
            
		} catch (NoSuchPropertyException e) {
			throw new RuntimeException("Field doesn't contain a script");
		} catch (Exception e) {
			throw new HtSyntaxException(e.getMessage());
		}		
	}	
	
	public static void execute (String statementList) 
	throws HtSyntaxException 
	{
		statementList = canonicalScriptForm(statementList);		
		
		SymbolFactory sf = new ComplexSymbolFactory();
		HtLexer lexer = new HtLexer(new ByteArrayInputStream(statementList.getBytes()), sf);
		
		HtInterpreter parser = new HtInterpreter(lexer, sf);	
		try {
			Symbol ast = parser.parse();
			Script script = (Script) ast.value;
			script.executeStatement();
		} catch (HtSyntaxException e) {
			throw e;
		} catch (Exception e) {
			throw new HtSyntaxException("Error parsing script");
		}
	}	
	
	public static String canonicalScriptForm (String scriptText) {
		return scriptText.trim() + "\n";
	}
}
