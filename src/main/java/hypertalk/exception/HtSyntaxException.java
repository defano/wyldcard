package hypertalk.exception;

public class HtSyntaxException extends HtException {

	private static final long serialVersionUID = -1451297529239807233L;

    public final int lineNumber, columnNumber;

	public HtSyntaxException(String message, int lineNumber, int columnNumber) {
        super(message);

        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
}
