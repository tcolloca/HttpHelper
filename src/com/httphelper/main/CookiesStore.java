package com.httphelper.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CookiesStore {

	public static void store(Set<Cookie> cookies, String pathName) throws HttpHelperException {
		String cookiesStr = String.join("\r\n", cookies.stream().map(Cookie::toString).collect(Collectors.toList()));
		Path path = Paths.get(pathName);
		try {
			if (Files.notExists(path.getParent())) {
				Files.createDirectory(path.getParent());
			}
			Files.write(path, cookiesStr.getBytes(),
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new HttpHelperException("Error while saving cookies", e);
		}
	}
	
	public static Set<Cookie> load(String dirPathName) throws HttpHelperException {
		Set<Cookie> cookies = new HashSet<>();
		Path path = Paths.get(dirPathName);
		if (Files.exists(path)) {
			try {
				cookies.addAll(Files.readAllLines(path).stream()
						.map(line -> new Cookie(line))
						.collect(Collectors.toList()));
			} catch (IOException e) {
				throw new HttpHelperException("Error while loading cookies", e);
			}
		}
		return cookies;
	}
}
