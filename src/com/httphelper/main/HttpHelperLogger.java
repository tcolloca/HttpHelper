package com.httphelper.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpHelperLogger {

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss");

  static void log(HttpResponse response, String dirPathName) {
    log(response,
        Paths.get(dirPathName + "/" + LocalDateTime.now().format(formatter) + "_res.html"));
  }

  static void log(HttpRequest request, String dirPathName) {
    log(request, Paths.get(dirPathName + "/" + LocalDateTime.now().format(formatter) + "_req.txt"));
  }

  static void log(Object obj, Path path) {
    try {
      if (Files.notExists(path.getParent())) {
        Files.createDirectory(path.getParent());
      }
      Files.write(path, obj.toString().getBytes(), StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new HttpHelperException("Error while saving logs", e);
    }
  }
}
