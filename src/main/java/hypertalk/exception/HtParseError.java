package hypertalk.exception;

import org.antlr.v4.runtime.ParserRuleContext;

public class HtParseError extends RuntimeException {
    public final int lineNumber, columnNumber;

    public HtParseError(ParserRuleContext ctx, String message) {
        super(message);

        this.lineNumber = ctx.getStart().getLine();
        this.columnNumber = ctx.getStart().getCharPositionInLine();
    }
}
