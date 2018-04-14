package com.defano.hypertalk.exception;

import com.defano.wyldcard.runtime.Breadcrumb;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;

public class HtSyntaxException extends HtException {

    public HtSyntaxException(RecognitionException e) {
        super(getFriendlyMessage(e));
        super.setBreadcrumb(new Breadcrumb(e.getOffendingToken()));
    }

    public HtSyntaxException(String errorMessage, Token offendingToken) {
        super(errorMessage);
        super.setBreadcrumb(new Breadcrumb(offendingToken));
    }

    private static String getFriendlyMessage(RecognitionException e) {

        if (e != null && e.getOffendingToken() != null) {
            Token t = e.getOffendingToken();

            if (t.getText().equals("<EOF>")) {
                return "Missing 'end' on line " + t.getLine() + ", column " + t.getCharPositionInLine() + ".";
            }

            return "Don't understand '" + t.getText() + "' on line " + t.getLine() + ", column " + t.getCharPositionInLine() + ".";
        }

        return "Don't understand that.";
    }
}
