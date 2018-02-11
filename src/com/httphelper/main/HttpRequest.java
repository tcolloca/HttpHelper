package com.httphelper.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class HttpRequest extends HttpMessage {

  private static final String PARAMS_DELIMETER = "&";

  private final HttpType type;
  private String path;
  private List<Parameter> queryParameters;
  private List<Parameter> formParameters;

  HttpRequest(HttpHelper helper, HttpType type) {
    super(helper);
    if (type == null) {
      throw new IllegalArgumentException("type is null");
    }
    this.type = type;
    this.queryParameters = new ArrayList<>();
    this.formParameters = new ArrayList<>();
  }

  public HttpRequest addQueryParameter(String key, String value) {
    if (key == null) {
      throw new IllegalArgumentException("key is null");
    }
    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }
    queryParameters.add(new Parameter(key, value));
    return this;
  }

  public HttpRequest addFormParameter(Parameter parameter) {
    if (parameter == null) {
      throw new IllegalArgumentException("parameter is null");
    }
    formParameters.add(parameter);
    return this;

  }

  public HttpRequest addFormParameter(String key, String value) {
    if (key == null) {
      throw new IllegalArgumentException("key is null");
    }
    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }
    return addFormParameter(new Parameter(key, value));
  }

  public HttpType getType() {
    return type;
  }

  public String getPath() {
    return path;
  }

  public HttpRequest setPath(String path) {
    if (path == null) {
      throw new IllegalArgumentException("path is null");
    }
    this.path = path;
    return this;
  }

  public List<Parameter> getQueryParameters() {
    return queryParameters;
  }

  public List<Parameter> getFormParameters() {
    return formParameters;
  }

  public HttpRequest addHeader(Header header) {
    super.addHeader(header);
    return this;
  }

  public HttpRequest addHeaders(List<Header> headers) {
    super.addHeaders(headers);
    return this;
  }

  public HttpRequest setVersion(HttpVersion version) {
    super.setVersion(version);
    return this;
  }

  public HttpResponse send() {
    return getHelper().send(this);
  }

  @Override
  public String toString() {
    String queryParametersStr = getQueryParametersString();
    String content = getContent();
    String pathStr = getPath(queryParametersStr);

    List<String> lines = new ArrayList<String>();
    lines.add(getType().getString() + " " + pathStr + " " + getVersion().getString());
    lines.addAll(getHeaders().stream().map(Header::toString).collect(Collectors.toList()));
    if (getType().equals(HttpType.POST) && !content.isEmpty()) {
      lines.add("content-length: " + content.length());
    }
    lines.add("");

    if (getType().equals(HttpType.POST) && !content.isEmpty()) {
      lines.add(content);
    }

    lines.add("");

    return String.join("\r\n", lines);
  }

  private String getQueryParametersString() {
    return String.join(PARAMS_DELIMETER,
        this.queryParameters.stream().map(Parameter::toString).collect(Collectors.toList()));
  }

  private String getPath(String queryParamsStr) {
    return queryParamsStr.isEmpty() ? getPath() : getPath() + "?" + queryParamsStr;
  }

  public String getContent() {
    return String.join(PARAMS_DELIMETER,
        this.formParameters.stream().map(Parameter::toString).collect(Collectors.toList()));
  }

  public HttpRequest addQueryParameter(String key, int value) {
    return addQueryParameter(key, String.valueOf(value));
  }

  public HttpRequest addFormParameter(String key, int value) {
    return addFormParameter(key, String.valueOf(value));
  }
}
