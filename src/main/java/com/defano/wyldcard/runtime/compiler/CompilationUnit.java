package com.defano.wyldcard.runtime.compiler;

import com.defano.hypertalk.parser.HyperTalkParser;
import org.antlr.v4.runtime.tree.ParseTree;

public enum CompilationUnit {
    /**
     * Represents a HyperTalk script assignable to a part (like a button or field); accepts whitespace, comments, and
     * zero or more handler/function definitions. Disallows arbitrary text or HyperTalk expressions/statements outside
     * of a handler.
     */
    SCRIPT,

    /**
     * Represents a list of zero or more, comma-delimited HyperTalk expressions as used to express argument lists,
     * points, rectangles, etc.
     */
    LIST_EXPRESSION,

    /**
     * Represents a list of executable HyperTalk statements or expressions as might be entered in the message box
     * or evaluated via the 'do' or 'the value of' commands. Accepts whitespace, comments, and zero or more HyperTalk
     * statements (including expression statements). Disallows handler/function definitions or arbitrary text.
     */
    SCRIPTLET;

    /**
     * Gets the root node of the abstract syntax tree associated with this CompilationUnit.
     * @param parser The Antlr4 HyperTalkParser object
     * @return The root node of the AST.
     */
    public ParseTree getParseTree(HyperTalkParser parser) {
        switch (this) {
            case SCRIPT:
                return parser.script();
            case SCRIPTLET:
                return parser.scriptlet();
            case LIST_EXPRESSION:
                return parser.listExpression();
        }

        throw new IllegalStateException("Bug! Unimplemented compilation unit.");
    }

}
