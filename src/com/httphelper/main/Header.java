package com.httphelper.main;


public class Header {

	private static final String delimiter = ":";
	
	private final String key;
	private final String value;

	public Header(String headerLine) {
		if (headerLine == null) {
			throw new IllegalArgumentException("headerLine is null");
		}
		String[] strs = headerLine.split(delimiter, 2);
		this.key = strs[0].trim().toLowerCase();
		this.value = strs[1].trim();
	}
	
	public Header(String key, String value) {
		if (key == null) {
			throw new IllegalArgumentException("key is null");
		}
		if (value == null) {
			throw new IllegalArgumentException("value is null");
		}
		this.key = key.trim().toLowerCase();
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return key + delimiter + " " + value;
	}
}
