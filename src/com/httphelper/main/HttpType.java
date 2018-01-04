package com.httphelper.main;

public enum HttpType {

	GET("GET"), POST("POST");
	
	private final String type;
	
	private HttpType(String type) {
		this.type = type;
	}

	public String getString() {
		return type;
	}
}
