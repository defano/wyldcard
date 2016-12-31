/**
 * Interpreter.java
 * @author matt.defano@gmail.com
 * 
 * Interpreter class provides static methods for compiling a script (translate
 * into an AST) as well as executing a string as a script.
 */

package hypercard.runtime;

import hypertalk.HyperTalkTreeVisitor;
import hypertalk.HypertalkErrorListener;
import hypertalk.ast.common.Script;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.HtException;
import hypertalk.exception.HtParseError;
import hypertalk.exception.HtSyntaxException;
import hypertalk.parser.HyperTalkLexer;
import hypertalk.parser.HyperTalkParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.concurrent.Future;

public class Interpreter {

    public static Script compile(String scriptText) throws HtException {

        HypertalkErrorListener errors = new HypertalkErrorListener();

        HyperTalkLexer lexer = new HyperTalkLexer(new ANTLRInputStream(scriptText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HyperTalkParser parser = new HyperTalkParser(tokens);
        parser.removeErrorListeners();        // don't log to console
        parser.addErrorListener(errors);

        try {
            ParseTree tree = parser.script();

            if (!errors.errors.isEmpty()) {
                throw errors.errors.get(0);
            }

            return (Script) new HyperTalkTreeVisitor().visit(tree);

        } catch (HtParseError e) {
            throw new HtSyntaxException(e.getMessage(), e.lineNumber, e.columnNumber);
        }
    }
    
    public static Future execute (PartSpecifier me, String statementList) throws HtException
    {
        return RuntimeEnv.getRuntimeEnv().executeStatementList(me, compile(statementList).getStatements(), true);
    }
}
