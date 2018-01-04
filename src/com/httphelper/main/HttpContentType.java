package com.httphelper.main;

public enum HttpContentType {

	APP_X_WWW_FORM_ENCODED("application/x-www-form-urlencoded");
	
	private final String type;
	
	private HttpContentType(String type) {
		this.type = type;
	}

	public String getString() {
		return type;
	}
}
