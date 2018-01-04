package com.httphelper.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpHelperLogger {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss");

	public static void log(HttpResponse response, String dirPathName) throws HttpHelperException {
		log(response, Paths.get(dirPathName + "/" + LocalDateTime.now().format(formatter) + "_res.html"));
	}
	
	public static void log(HttpRequest request, String dirPathName) throws HttpHelperException {
		log(request, Paths.get(dirPathName + "/" + LocalDateTime.now().format(formatter) + "_req.txt"));
	}
	
	public static void log(Object obj, Path path) throws HttpHelperException {
		try {
			if (Files.notExists(path.getParent())) {
				Files.createDirectory(path.getParent());
			}
			Files.write(path, obj.toString().getBytes(),
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new HttpHelperException("Error while saving logs", e);
		}
	}
}
