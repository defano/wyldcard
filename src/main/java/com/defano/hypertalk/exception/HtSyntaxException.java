package com.defano.hypertalk.exception;

import com.defano.wyldcard.runtime.Breadcrumb;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

public class HtSyntaxException extends HtException {

    public HtSyntaxException(RecognitionException e) {
        super(getFriendlyMessage(e.getOffendingToken()));
        super.setBreadcrumb(new Breadcrumb(e.getOffendingToken()));
    }

    public HtSyntaxException(Token offendingToken) {
        super(getFriendlyMessage(offendingToken));
        super.setBreadcrumb(new Breadcrumb(offendingToken));
    }

    private static String getFriendlyMessage(Token t) {
        if (t.getText().equals("<EOF>")) {
            return "Missing 'end' on line " + t.getLine() + ", column " + t.getCharPositionInLine() + ".";
        }

        return "Don't understand '" + t.getText() + "' on line " + t.getLine() + ", column " + t.getCharPositionInLine() + ".";
    }
}
