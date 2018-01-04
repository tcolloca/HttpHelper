package com.httphelper.main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Headers {

	public static Header host(String host) {
		if (host == null) {
			throw new IllegalArgumentException("host is null");
		}
		return getHeader("Host", host);
	}
	
	public static Header contentType(HttpContentType contentType) {
		return getHeader("Content-Type", contentType.getString());
	}

	public static Header referer(String referer) {
		return getHeader("Referer", referer);
	}
	
	static Header contentLength(int length) {
		return getHeader("Content-Length", Integer.toString(length));
	}
	
	static Header cookie(Cookie cookie) {
		return cookie(new HashSet<>(Arrays.asList(cookie)));
	}
	
	static Header cookie(Set<Cookie> cookies) {
		return getHeader("Cookie", String.join("; ", cookies.stream()
				.map(Cookie::toString)
				.collect(Collectors.toList())));
	}
	
	private static Header getHeader(String key, String value) {
		return new Header(key, value);
	}
}
