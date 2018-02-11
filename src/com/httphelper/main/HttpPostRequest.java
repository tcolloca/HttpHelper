package com.httphelper.main;


class HttpPostRequest extends HttpRequest {

  HttpPostRequest(HttpHelper helper) {
    super(helper, HttpType.POST);
  }
}
