package com.httphelper.main;



public class Test {

  public static void main(String[] args) {
    HttpHelper helper = new HttpHelper("guiamt.net").setDefaultHost("guiamt.net");

    HttpHelper.storeCookies = true;
    HttpHelper.log = true;

    long ini = System.nanoTime();
    HttpRequest req =
        helper.post().addHeader(Headers.contentType(HttpContentType.APP_X_WWW_FORM_ENCODED))
            .addQueryParameter("id", "tu_cuenta").addFormParameter("logUser", "tomatereloco")
            .addFormParameter("logPassword", "tomas123").addFormParameter("remember", "1")
            .addFormParameter("login", "Login");
    req.send();
    long end = System.nanoTime();
    System.out.println((end - ini) / 1e9);

    ini = System.nanoTime();
    helper.get().send();
    end = System.nanoTime();
    System.out.println((end - ini) / 1e9);
  }
}
