package com.defano.hypertalk;

import com.defano.hypertalk.exception.HtSyntaxException;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;

public class HyperTalkErrorListener extends BaseErrorListener {

    public final List<HtSyntaxException> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {

        if (offendingSymbol instanceof Token) {
            errors.add(new HtSyntaxException((Token) offendingSymbol, e.getExpectedTokens()));
        }
    }
}
