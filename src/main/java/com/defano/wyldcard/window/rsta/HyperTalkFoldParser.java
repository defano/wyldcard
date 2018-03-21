package com.defano.wyldcard.window.rsta;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;

public class HyperTalkFoldParser extends CurlyFoldParser {

    private String foldedIdentifier;

    @Override
    public boolean isLeftCurly(Token t) {
        if (t.getLexeme().equalsIgnoreCase("on") || t.getLexeme().equalsIgnoreCase("function")) {
            if (t.getNextToken() != null && t.getNextToken().isWhitespace()) {
                if (t.getNextToken().getNextToken() != null) {
                    foldedIdentifier = t.getNextToken().getNextToken().getLexeme();
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isRightCurly(Token t) {
        if (t.getLexeme().equalsIgnoreCase("end")) {
            if (t.getNextToken() != null && t.getNextToken().isWhitespace()) {
                Token identifierToken = t.getNextToken().getNextToken();
                return identifierToken != null && identifierToken.getLexeme() != null && identifierToken.getLexeme().equalsIgnoreCase(foldedIdentifier);
            }
        }

        return false;
    }
}
