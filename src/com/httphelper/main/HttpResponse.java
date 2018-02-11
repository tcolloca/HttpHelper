package com.httphelper.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HttpResponse extends HttpMessage {

  private int statusCode;
  private String statusMessage;
  private final Optional<String> content;
  private byte[] contentBytes;

  HttpResponse(HttpHelper helper, String response, byte[] bytes) {
    super(helper);

    Scanner sc = new Scanner(response);
    try {
      parseFirstLine(sc.nextLine().trim());
      parseHeaders(sc);
      this.content = parseContent(sc);
      parseContentBytes(bytes);
    } catch (Exception e) {
      throw new IllegalStateException("Invalid response: " + response);
    } finally {
      sc.close();
    }
  }

  private void parseFirstLine(String fstLine) throws HttpResponseParseException {
    String[] fstLineParts = fstLine.split(" ", 3);
    if (fstLineParts.length != 3) {
      throw new HttpResponseParseException();
    }
    setVersion(HttpVersion.getVersion(fstLineParts[0]));
    this.statusCode = Integer.parseInt(fstLineParts[1]);
    this.statusMessage = fstLineParts[2];
  }

  private void parseHeaders(Scanner sc) {
    String line;
    while (sc.hasNextLine() && !(line = sc.nextLine()).trim().isEmpty()) {
      addHeader(new Header(line));
    }
  }

  private Optional<String> parseContent(Scanner sc) {
    sc.useDelimiter("\0");
    return sc.hasNext() ? Optional.of(sc.next()) : Optional.empty();
  }

  private void parseContentBytes(byte[] bytes) {
    int contentLength = Integer.valueOf(getHeaderValue("content-length").orElse("0"));
    this.contentBytes = new byte[contentLength];
    System.arraycopy(bytes, bytes.length - contentLength, this.contentBytes, 0, contentLength);
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public Optional<String> getContent() {
    return content;
  }

  public byte[] getContentBytes() {
    return contentBytes;
  }

  public HttpResponse setVersion(HttpVersion version) {
    super.setVersion(version);
    return this;
  }

  public HttpResponse addHeader(Header header) {
    super.addHeader(header);
    return this;
  }

  public HttpResponse addHeaders(List<Header> headers) {
    super.addHeaders(headers);
    return this;
  }

  @Override
  public String toString() {
    List<String> lines = new ArrayList<>();
    lines.add(getVersion().getString() + " " + getStatusCode() + " " + getStatusMessage());
    lines.addAll(getHeaders().stream().map(Header::toString).collect(Collectors.toList()));
    lines.add("");

    if (content.isPresent()) {
      lines.add(content.get());
      lines.add("");
    }

    return String.join("\r\n", lines);
  }
}
