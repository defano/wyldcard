package hypertalk;

import hypertalk.exception.HtSyntaxException;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class HypertalkErrorListener extends BaseErrorListener {

    public final List<HtSyntaxException> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        errors.add(new HtSyntaxException(msg, line, charPositionInLine));
    }
}
