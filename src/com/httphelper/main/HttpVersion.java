package com.httphelper.main;

public enum HttpVersion {

  V1_0("HTTP/1.0"), V1_1("HTTP/1.1");

  private final String version;

  private HttpVersion(String version) {
    this.version = version;
  }

  public String getString() {
    return version;
  }

  public static HttpVersion getVersion(String versionStr) {
    for (HttpVersion version : HttpVersion.values()) {
      if (version.getString().equals(versionStr)) {
        return version;
      }
    }
    throw new IllegalStateException("Unknown HTTP version: " + versionStr);
  }
}
