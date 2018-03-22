package com.defano.hypertalk.exception;

import com.defano.wyldcard.runtime.Breadcrumb;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;

public class HtSyntaxException extends HtException {

    public IntervalSet expectedTokens;

    public HtSyntaxException(RecognitionException e) {
        super(getFriendlyMessage(e.getOffendingToken()));
        super.setBreadcrumb(new Breadcrumb(e.getOffendingToken()));
    }

    public HtSyntaxException(Token offendingToken, IntervalSet expectedTokens) {
        super(getFriendlyMessage(offendingToken));
        super.setBreadcrumb(new Breadcrumb(offendingToken));
        this.expectedTokens = expectedTokens;
    }

    private static String getFriendlyMessage(Token t) {
        if (t.getText().equals("<EOF>")) {
            return "Missing 'end' on line " + t.getLine() + ", column " + t.getCharPositionInLine() + ".";
        }

        return "Don't understand '" + t.getText() + "' on line " + t.getLine() + ", column " + t.getCharPositionInLine() + ".";
    }
}
