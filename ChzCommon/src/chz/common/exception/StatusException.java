package chz.common.exception;

public class StatusException extends Exception {

	public StatusException() {
		super();
	}

	public StatusException(String message, Throwable cause) {
		super(message, cause);
	}

	public StatusException(String message) {
		super(message);
	}

	public StatusException(Throwable cause) {
		super(cause);
	}

}
