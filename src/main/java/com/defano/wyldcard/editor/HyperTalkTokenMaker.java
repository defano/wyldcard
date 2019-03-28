package com.defano.wyldcard.editor;

import com.defano.hypertalk.parser.HyperTalkLexer;
import com.defano.wyldcard.runtime.compiler.CaseInsensitiveInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

import javax.swing.text.Segment;
import java.util.ArrayList;

/**
 * Uses the HyperTalk Antlr lexer to generate tokens and their syntax classification useful for syntax highlighting
 * by {@link org.fife.ui.rsyntaxtextarea.RSyntaxTextArea}.
 * <p>
 * Based on code published by Gary Ford, on GitHub:
 * https://github.com/fjenett/processing-rsyntaxtextarea-antlr4
 */
@SuppressWarnings("unused")
public class HyperTalkTokenMaker extends AbstractTokenMaker {

    private final ArrayList<String> keywords = new ArrayList<>();
    private HyperTalkLexer antlr;

    @SuppressWarnings("unused")
    public HyperTalkTokenMaker() {
        for (int index = 0; index < HyperTalkLexer.VOCABULARY.getMaxTokenType(); index++) {
            keywords.add(HyperTalkLexer.VOCABULARY.getLiteralName(index));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLastTokenTypeOnLine(Segment pSegment, int initialTokenType) {
        antlr = new HyperTalkLexer(new CaseInsensitiveInputStream(pSegment.array, pSegment.offset, pSegment.count));
        BufferedTokenStream tokenStream = new BufferedTokenStream(antlr);

        while (true) {
            org.antlr.v4.runtime.Token token = tokenStream.LT(1);

            if (token.getType() == org.antlr.v4.runtime.Token.EOF) {
                return TokenTypes.NULL;
            }

            tokenStream.consume();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token getTokenList(Segment pSegment, int initialTokenType, int pSegmentOffset) {
        antlr = new HyperTalkLexer(new CaseInsensitiveInputStream(pSegment.array, pSegment.offset, pSegment.count));
        BufferedTokenStream tokenStream = new BufferedTokenStream(antlr);

        resetTokenList(); // so we can create a fresh token list
        org.antlr.v4.runtime.Token lastToken = null;

        // retrieve and convert tokens one at a time; note that
        // the stream returns the EOF token as the last token
        while (true) {
            org.antlr.v4.runtime.Token token = tokenStream.LT(1);

            if (token.getType() == org.antlr.v4.runtime.Token.EOF) {
                if (lastToken == null) {
                    addNullToken();
                }
                break;
            }

            lastToken = token;
            tokenStream.consume();

            // convert the ANTLR token to a RSyntaxTextArea token and add it to the linked list
            int tokenStart = token.getCharPositionInLine() + pSegment.offset;
            int tokenEnd = tokenStart + token.getText().length() - 1;
            int tokenOffset = pSegmentOffset + token.getCharPositionInLine();

            addToken(pSegment.array, tokenStart, tokenEnd, classifyToken(token), tokenOffset);
        }

        return super.firstToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenMap getWordsToHighlight() {
        return wordsToHighlight;
    }

    /**
     * Converts an Antlr4 token to an RSyntaxTextArea token type (as defined in {@link TokenTypes}. That is, this method
     * classifies tokens so they can be highlighted accordingly.
     *
     * @param token The token to classify
     * @return The token type
     */
    private int classifyToken(org.antlr.v4.runtime.Token token) {

        // Attempt to determine token by type
        switch (token.getType()) {
            case HyperTalkLexer.ID:
                return TokenTypes.IDENTIFIER;
            case HyperTalkLexer.WHITESPACE:
                return TokenTypes.WHITESPACE;
            case HyperTalkLexer.NUMBER_LITERAL:
            case HyperTalkLexer.INTEGER_LITERAL:
                return TokenTypes.LITERAL_NUMBER_DECIMAL_INT;
            case HyperTalkLexer.STRING_LITERAL:
                return TokenTypes.LITERAL_STRING_DOUBLE_QUOTE;
            case HyperTalkLexer.COMMENT:
                return TokenTypes.COMMENT_EOL;
        }

        // If that fails, try to determine token by lexeme
        switch (token.getText().toLowerCase()) {
            case "true":
            case "false":
                return TokenTypes.LITERAL_BOOLEAN;

            case ",":
            case "(":
            case ")":
                return TokenTypes.SEPARATOR;

            case "-":
            case "not":
            case "there is a":
            case "this is an":
            case "this is no":
            case "this is not a":
            case "this is not an":
            case "^":
            case "mod":
            case "div":
            case "/":
            case "*":
            case "+":
            case "&":
            case "&&":
            case ">=":
            case "<=":
            case "≤":
            case "≥":
            case ">":
            case "<":
            case "contains":
            case "is in":
            case "is not in":
            case "is a":
            case "is an":
            case "is not a":
            case "is not an":
            case "is within":
            case "is not within":
            case "=":
            case "is not":
            case "is":
            case "<>":
            case "≠":
            case "and":
            case "or":
                return TokenTypes.OPERATOR;
        }

        // Does the token appear in the grammar's list of lexed symbols (keywords)?
        if (keywords.contains("'" + token.getText().toLowerCase() + "'")) {
            return TokenTypes.RESERVED_WORD;
        }

        // Anything else we missed...
        return TokenTypes.PREPROCESSOR;
    }

}