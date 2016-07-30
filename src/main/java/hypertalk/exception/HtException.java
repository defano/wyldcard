package hypertalk.exception;

public class HtException extends Exception {

    public HtException(Throwable cause) {
        super(cause);
    }

	public HtException(String message) {
        super(message);
    }
}
