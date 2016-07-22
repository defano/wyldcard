package hypertalk.exception;

import org.antlr.v4.runtime.ParserRuleContext;

public class HtSyntaxException extends HtException {

    public final int lineNumber, columnNumber;

    public HtSyntaxException(String message, int lineNumber, int columnNumber) {
        super(message);

        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
}
