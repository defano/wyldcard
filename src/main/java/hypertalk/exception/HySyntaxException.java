package hypertalk.exception;

public class HySyntaxException extends HtException {

	private static final long serialVersionUID = -1451297529239807233L;

	public HySyntaxException(String message, int lineNumber, int columnNumber) {
        super(message);
    }
}
