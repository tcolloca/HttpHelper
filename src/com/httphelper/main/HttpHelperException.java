package com.httphelper.main;

public class HttpHelperException extends Exception {

	private static final long serialVersionUID = 6680371871218325421L;

	HttpHelperException(String message, Exception cause) {
		super(message, cause);
	}

	HttpHelperException(String cause) {
		super(cause);
	}
}
