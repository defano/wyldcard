package hypertalk;

import hypertalk.exception.HySyntaxException;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class HypertalkErrorListener extends BaseErrorListener {

    public final List<HySyntaxException> errors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        errors.add(new HySyntaxException(msg, line, charPositionInLine));
    }
}
