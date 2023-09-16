package com.httphelper.main;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Cookie {

  private static final List<DateTimeFormatter> FORMATTERS =
      Arrays.asList(DateTimeFormatter.ofPattern("EEE, d-MMM-uuuu H:m:s z", Locale.ENGLISH));

  private static final String EXPIRES_KEY = "expires=";

  private final CookieKeyValue cookieKeyValue;
  private final Optional<ZonedDateTime> expirationDate;

  public Cookie(String key, String value, Optional<ZonedDateTime> expirationDate) {
    this.cookieKeyValue = new CookieKeyValue(key, value);
    this.expirationDate = expirationDate;
  }

  Cookie(String cookie) {
    String[] strs = cookie.split("; ");
    this.cookieKeyValue = new CookieKeyValue(strs[0]);
    int expiresIndex = getExpirationDateIndex(strs);
    this.expirationDate = expiresIndex < 0 ? Optional.empty()
        : Optional.ofNullable(getZonedDateTime(strs[expiresIndex].replace(EXPIRES_KEY, "")));
  }

  private ZonedDateTime getZonedDateTime(String dateStr) {
    for (DateTimeFormatter formatter : FORMATTERS) {
      try {
        try {
          return ZonedDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
          return LocalDate.parse(dateStr, formatter).atStartOfDay().atZone(ZoneId.of("GMT"));
        }
      } catch (DateTimeParseException e) {
      }
    }
    return null;
  }

  private int getExpirationDateIndex(String[] parts) {
    for (int i = 0; i < parts.length; i++) {
      if (parts[i].startsWith(EXPIRES_KEY)) {
        return i;
      }
    }
    return -1;
  }

  String getCookie() {
    return cookieKeyValue.toString();
  }

  public String getName() {
    return cookieKeyValue.getKey();
  }

  public String getValue() {
    return cookieKeyValue.getValue();
  }

  public Optional<ZonedDateTime> getExpirationDate() {
    return expirationDate;
  }

  boolean isValid() {
    if (!expirationDate.isPresent()) {
      return true;
    }
    return ZonedDateTime.now().isBefore(expirationDate.get());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cookieKeyValue == null) ? 0 : cookieKeyValue.hashCode());
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
    Cookie other = (Cookie) obj;
    if (cookieKeyValue == null) {
      if (other.cookieKeyValue != null)
        return false;
    } else if (!cookieKeyValue.equals(other.cookieKeyValue))
      return false;
    return true;
  }

  @Override
  public String toString() {
    String expirationDateStr =
        expirationDate.isPresent() ? EXPIRES_KEY + expirationDate.get().format(FORMATTERS.get(0))
            : "";
    return cookieKeyValue.toString() + "; " + expirationDateStr;
  }
}
