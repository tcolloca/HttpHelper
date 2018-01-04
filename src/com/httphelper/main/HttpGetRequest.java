package com.httphelper.main;


class HttpGetRequest extends HttpRequest {

	HttpGetRequest(HttpHelper helper) {
		super(helper, HttpType.GET);
	}
}
