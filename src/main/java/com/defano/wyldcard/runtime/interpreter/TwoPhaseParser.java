package com.defano.wyldcard.runtime.interpreter;

import com.defano.hypertalk.HyperTalkErrorListener;
import com.defano.hypertalk.HyperTalkTreeVisitor;
import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.exception.HtSyntaxException;
import com.defano.hypertalk.parser.HyperTalkLexer;
import com.defano.hypertalk.parser.HyperTalkParser;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

public class TwoPhaseParser {

    /**
     * Performs a two-phase parse of the given HyperTalk script text. First attempts to parse the script using the SLL
     * prediction mode; if that fails, attempts to re-parse the input using the LL prediction mode.
     *
     * See: http://www.antlr.org/api/Java/org/antlr/v4/runtime/atn/PredictionMode.html
     *
     * @param compilationUnit The unit of work to compile/parse. Represents the grammar's start symbol that should be
     *                        used.
     * @param scriptText A plaintext representation of the HyperTalk script to parse
     * @return The root of the abstract syntax tree associated with the given compilation unit (i.e., {@link Script}).
     * @throws HtSyntaxException Thrown if an error occurs while parsing the script.
     */
    public static Object parseScript(CompilationUnit compilationUnit, String scriptText) throws HtSyntaxException {

        // Nothing to do for empty scripts
        if (scriptText == null || scriptText.length() == 0) {
            return new Script();
        }

        Object parseTree = parseSLL(compilationUnit, scriptText);

        if (parseTree == null) {
            parseTree = parseLL(compilationUnit, scriptText);
        }

        return parseTree;
    }

    /**
     * "Second phase" parsing attempt. Will accept any valid HyperTalk script entry, but is less performant for inputs
     * utilizing certain parts of the grammar.
     *
     * @param compilationUnit The unit of work to compile/parse. Represents the grammar's start symbol that should be
     *                        used.
     * @param scriptText A plaintext representation of the HyperTalk script to parse
     * @return The root of the abstract syntax tree associated with the given compilation unit (i.e., {@link Script}).
     * @throws HtSyntaxException Thrown if an error occurs while parsing the script.
     */
    private static Object parseLL(CompilationUnit compilationUnit, String scriptText) throws HtSyntaxException {
        HyperTalkErrorListener errors = new HyperTalkErrorListener();
        HyperTalkLexer lexer = new HyperTalkLexer(new CaseInsensitiveInputStream(scriptText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HyperTalkParser parser = new HyperTalkParser(tokens);

        parser.setErrorHandler(new DefaultErrorStrategy());
        parser.getInterpreter().setPredictionMode(PredictionMode.LL);
        parser.removeErrorListeners();        // don't log to console
        parser.addErrorListener(errors);

        try {
            ParseTree tree = compilationUnit.getParseTree(parser);

            if (!errors.errors.isEmpty()) {
                throw errors.errors.get(0);
            }

            return new HyperTalkTreeVisitor().visit(tree);
        } catch (RecognitionException e) {
            throw new HtSyntaxException(e);
        }
    }

    /**
     * "First phase" parsing attempt. Provides better performance than {@link #parseLL(CompilationUnit, String)}, but
     * will erroneously report syntax errors when parsing script text utilizing certain, ambiguous, parts of the
     * grammar.
     *
     * @param compilationUnit The unit of work to compile/parse. Represents the grammar's start symbol that should be
     *                        used.
     * @param scriptText A plaintext representation of the HyperTalk script to parse
     * @return The root of the abstract syntax tree associated with the given compilation unit (i.e., {@link Script}),
     * or null if parsing fails.
     */
    private static Object parseSLL(CompilationUnit compilationUnit, String scriptText) {
        HyperTalkLexer lexer = new HyperTalkLexer(new CaseInsensitiveInputStream(scriptText));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HyperTalkParser parser = new HyperTalkParser(tokens);

        parser.setErrorHandler(new BailErrorStrategy());
        parser.removeErrorListeners();
        parser.getInterpreter().setPredictionMode(PredictionMode.SLL);

        try {
            ParseTree tree = compilationUnit.getParseTree(parser);
            return new HyperTalkTreeVisitor().visit(tree);
        } catch (ParseCancellationException e) {
            return null;
        }
    }
}
