/**
 * Interpreter.java
 * @author matt.defano@gmail.com
 * 
 * Interpreter class provides static methods for compiling a script (translate
 * into an AST) as well as executing a string as a script.
 */

package hypercard.runtime;

import hypertalk.HypertalkErrorListener;
import hypertalk.HypertalkTreeVisitor;
import hypertalk.ast.common.Script;
import hypertalk.exception.HtException;
import hypertalk.exception.HtParseError;
import hypertalk.exception.HtSyntaxException;
import hypertalk.parser.HypertalkLexer;
import hypertalk.parser.HypertalkParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Interpreter {

	public static Script compile(String scriptText) throws HtException {

		scriptText = canonicalScriptForm(scriptText);
		HypertalkErrorListener errors = new HypertalkErrorListener();

		HypertalkLexer lexer = new HypertalkLexer(new ANTLRInputStream(scriptText));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		HypertalkParser parser = new HypertalkParser(tokens);
		parser.addErrorListener(errors);

		try {
			ParseTree tree = parser.script();

			if (!errors.errors.isEmpty()) {
				throw errors.errors.get(0);
			}

			return (Script) new HypertalkTreeVisitor().visit(tree);

		} catch (HtParseError e) {
			throw new HtSyntaxException(e.getMessage(), e.lineNumber, e.columnNumber);
		}
	}
	
	public static void execute (String statementList) throws HtException
	{
		compile(statementList).executeStatement();
	}	
	
	private static String canonicalScriptForm (String scriptText) {
		return scriptText.trim() + "\n";
	}
}
