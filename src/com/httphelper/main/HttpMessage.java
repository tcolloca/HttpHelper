package com.httphelper.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpMessage {

  private final HttpHelper helper;
  private HttpVersion version;
  private Map<String, List<String>> headers;

  public HttpMessage(HttpHelper helper) {
    super();
    this.helper = helper;
    this.headers = new HashMap<>();
  }

  HttpMessage addHeader(Header header) {
    return addHeaders(Arrays.asList(header));
  }

  HttpMessage addHeaders(List<Header> headers) {
    if (headers == null) {
      throw new IllegalArgumentException("headers is null");
    }
    if (headers.contains(null)) {
      throw new IllegalArgumentException("A header is null");
    }
    headers.forEach(header -> {
      this.headers.putIfAbsent(header.getKey(), new ArrayList<>());
      this.headers.get(header.getKey()).add(header.getValue());
    });
    return this;
  }

  public List<String> getHeaderValues(String key) {
    if (key == null) {
      throw new IllegalArgumentException("key is null");
    }
    List<String> headersList = headers.get(key);
    if (headersList == null) {
      return new ArrayList<>();
    }
    return headersList;
  }

  public Optional<String> getHeaderValue(String key) {
    List<String> headersList = getHeaderValues(key);
    if (headersList.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(headersList.get(0));
  }

  public HttpVersion getVersion() {
    return version;
  }

  HttpMessage setVersion(HttpVersion version) {
    if (version == null) {
      throw new IllegalArgumentException("version is null");
    }
    this.version = version;
    return this;
  }

  public List<Header> getHeaders() {
    return headers.entrySet().stream().map(
        e -> e.getValue().stream().map(v -> new Header(e.getKey(), v)).collect(Collectors.toList()))
        .flatMap(List::stream).collect(Collectors.toList());
  }

  HttpHelper getHelper() {
    return helper;
  }
}
