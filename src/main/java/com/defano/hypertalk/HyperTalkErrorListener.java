package com.defano.hypertalk;

import com.defano.hypertalk.exception.HtSyntaxException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.List;

public class HyperTalkErrorListener extends BaseErrorListener {

    public final List<HtSyntaxException> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        if (e != null) {
            errors.add(new HtSyntaxException(e));
        }
    }
}
