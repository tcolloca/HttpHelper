package com.httphelper.main;

class CookieKeyValue {

  private static final String DELIMETER = "=";

  private final String key;
  private final String value;

  CookieKeyValue(String cookieKeyValue) {
    String[] strs = cookieKeyValue.split(DELIMETER);
    this.key = strs[0];
    this.value = strs[1];
  }

  CookieKeyValue(String key, String value) {
    super();
    this.key = key;
    this.value = value;
  }

  @Override
  public String toString() {
    return key + DELIMETER + value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((key == null) ? 0 : key.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CookieKeyValue other = (CookieKeyValue) obj;
    if (key == null) {
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;
    return true;
  }

  String getKey() {
    return key;
  }

  String getValue() {
    return value;
  }
}
