package com.tsf;

public class TsfException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 0;
	private String message;
	private Throwable cause;

	/**
	 * Constructs a JSONException with an explanatory message.
	 * 
	 * @param message
	 *            Detail about the reason for the exception.
	 */
	public TsfException(String message) {
		super(message);
		this.message = message;
	}

	public TsfException(Throwable t) {
		super(t.getMessage());
		this.cause = t;
	}

	public TsfException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
		this.cause = cause;
	}

	public Throwable getCause() {
		return this.cause;
	}

	public String getMessage() {
		return message;
	}
}
