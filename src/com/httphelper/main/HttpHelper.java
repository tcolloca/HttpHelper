package com.httphelper.main;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class HttpHelper {

	private static final int BUFF_SIZE = 65536;
	
	public static String logPath = "logs";
	public static boolean log = true;
	public static String cookiesPath = "cookies/cookies.txt";
	public static boolean storeCookies = true;
	
	private final String host;
	private final int port;

	private final Set<Cookie> cookies;
	private HttpVersion defaultVersion = HttpVersion.V1_1;
	private final List<Header> defaultHeaders;
	private String defaultHost;
	private boolean keepAlive;
	private Socket socket;
	
	private boolean useHttps;
	private HttpURLConnection httpConnection;
	
	private boolean shouldRedirect = true;

	private double minDelay;
	private double maxDelay;
	
	public HttpHelper(String host) throws HttpHelperException {
		this(host, 80);
	}
	
	private HttpHelper(String host, int port, HttpVersion defaultVersion,
			String defaultHost, boolean keepAlive,
			double minDelay, double maxDelay, List<Header> defaultHeaders, Set<Cookie> cookies,
			boolean useHttps) throws HttpHelperException {
		super();
		this.host = host;
		this.port = port;
		this.defaultVersion = defaultVersion;
		this.defaultHost = defaultHost;
		this.keepAlive = keepAlive;
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		this.defaultHeaders = defaultHeaders;
		this.cookies = cookies;
		this.useHttps = useHttps;
		
		try {
			this.socket = new Socket(InetAddress.getByName(host), port);
		} catch (Exception e) {
			throw new HttpHelperException("Failed to open socket.", e);
		}
	}

	public HttpHelper(String host, int port) throws HttpHelperException {
		if (host == null) {
			throw new IllegalArgumentException("host is null");
		}
		this.host = host;
		this.port = port;
		this.defaultHost = host;
		this.defaultHeaders = new ArrayList<>();
		this.cookies = new HashSet<>();
		
		try {
			this.socket = new Socket(InetAddress.getByName(host), port);
		} catch (Exception e) {
			throw new HttpHelperException("Failed to open socket.", e);
		}
		
		cookies.addAll(CookiesStore.load(cookiesPath));
	}
	
	public HttpHelper setDefaultVersion(HttpVersion version) {
		if (version == null) {
			throw new IllegalArgumentException("version is null");
		}
		this.defaultVersion = version;
		if (version.equals(HttpVersion.V1_1)) {
			setKeepAlive(true);
		} else {
			setKeepAlive(false);
		}
		return this;
	}
	
	public HttpHelper setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}
	
	public HttpHelper setRandomDelay(double minDelay, double maxDelay) {
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		return this;
	}
	
	public HttpHelper useHttps() {
		this.useHttps = true;
		return this;
	}

	public HttpHelper addDefaultHeader(String headerLine) {
		return addDefaultHeader(new Header(headerLine));
	}
	
	public HttpHelper addDefaultHeader(String key, String value) {
		return addDefaultHeader(new Header(key, value));
	}
	
	public HttpHelper addDefaultHeader(Header header) {
		if (header == null) {
			throw new IllegalArgumentException("header is null");
		}
		this.defaultHeaders.add(header);
		return this;
	}
	
	public HttpHelper setDefaultHost(String host) {
		if (host == null) {
			throw new IllegalArgumentException("host is null");
		}
		this.defaultHost = host;
		return this;
	}
	
	public HttpRequest get() {
		return get("/");
	}
	
	public synchronized Set<Cookie> validCookies() {
		cookies.addAll(cookies.stream().filter(Cookie::isValid).collect(Collectors.toSet()));
		return cookies;
	}

	public HttpRequest get(String path) {
		HttpRequest req = new HttpGetRequest(this).setVersion(defaultVersion)
				.setPath(path)
				.addHeader(Headers.host(defaultHost))
				.addHeaders(defaultHeaders);
		if (!cookies.isEmpty()) {
			req.addHeader(Headers.cookie(validCookies()));
		}
		return req;
	}
	
	public HttpRequest post() {
		return post("/");
	}

	public HttpRequest post(String path) {
		HttpRequest req = new HttpPostRequest(this).setVersion(defaultVersion)
				.setPath(path)
				.addHeader(Headers.host(defaultHost))
				.addHeaders(defaultHeaders);
		if (!cookies.isEmpty()) {
			req.addHeader(Headers.cookie(validCookies()));
		}
		return req;
	}
	
	public synchronized HttpResponse send(HttpRequest httpRequest) throws HttpHelperException {
		delay();
		try {
			if (useHttps) {
				sendHttps(httpRequest);
			} else {
				// TODO :: Migrate to http connection
				sendHttp(httpRequest);
			}
		} catch (IOException e) {
			throw new HttpHelperException("Failed to send request.", e);
		}
		if (log) {
			HttpHelperLogger.log(httpRequest, logPath);
		}
		return waitResponse(httpRequest);
	}
	
	private void sendHttps(HttpRequest httpRequest) throws IOException {
		httpConnection = (HttpURLConnection) new URL("https://" + host + httpRequest.getPath()).openConnection();
		for (Header header : httpRequest.getHeaders()) {
			httpConnection.addRequestProperty(header.getKey(), header.getValue());
		}
		httpConnection.setRequestMethod(httpRequest.getType().getString());
		String content = httpRequest.getContent();
		if (!content.isEmpty()) {
			httpConnection.addRequestProperty("Content-Length", String.valueOf(content.length()));
			httpConnection.setDoOutput(true);
			PrintWriter pw = new PrintWriter(httpConnection.getOutputStream());
			pw.print(httpRequest.getContent());
			pw.flush();
		}
	}
	
	private void sendHttp(HttpRequest httpRequest) throws IOException {
		if (!keepAlive || socket.isClosed()) {
			try {
				socket = new Socket(InetAddress.getByName(host), port);
			} catch (UnknownHostException e) {
				throw new IllegalStateException("Host should be known at this point.", e);
			}
		}
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		pw.print(httpRequest);
		pw.flush();
	}

	private void delay() {
		if (minDelay > 0 && maxDelay > 0 && minDelay < maxDelay) {
			int minMillis = (int) (minDelay * 1000);
			int maxMillis = (int) (maxDelay * 1000);
			try {
				Thread.sleep(new Random().nextInt(maxMillis - minMillis) + minMillis);
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	private HttpResponse waitResponse(HttpRequest req) throws HttpHelperException {
		if (socket.isClosed()) {
			throw new HttpHelperException("Socket is closed!");
		}
		StringBuilder responseStr = new StringBuilder();
		ByteInfo byteInfo = new ByteInfo(new byte[BUFF_SIZE]);
		try {			
			if (useHttps) {
				appendHttpsHeaders(byteInfo, responseStr);
			}
			readBytes(byteInfo, responseStr);
		} catch (IOException e) {
			throw new HttpHelperException("Failed waiting response.", e);
		}
		
		HttpResponse response = new HttpResponse(this, responseStr.toString(), 
				Arrays.copyOf(byteInfo.arr, byteInfo.size));
		
		handleCookies(response);
		
		if (log) {
			HttpHelperLogger.log(response, logPath);
		}
		
		if (shouldRedirect && response.getStatusCode() == 302) {
			return get(getLocation(req, response)).send();
		}
		
		return response;
	}
	
	private void appendHttpsHeaders(ByteInfo byteInfo, StringBuilder responseStr) throws IOException {
		responseStr.append("HTTP/1.1 ");
		responseStr.append(httpConnection.getResponseCode());
		responseStr.append(" ");
		responseStr.append(httpConnection.getResponseMessage());
		responseStr.append("\r\n");
		
		httpConnection.getHeaderFields().entrySet().stream()
			.filter(e -> e.getKey() != null)
			.forEach(e -> e.getValue().forEach(v -> responseStr.append(e.getKey() + ": " + v + "\r\n")));
		responseStr.append("\r\n");
		
		copyFromBuffer(byteInfo, responseStr.toString().getBytes(), responseStr.length());
	}
	
	private void readBytes(ByteInfo byteInfo, StringBuilder responseStr) throws IOException {
		InputStream in = useHttps ? httpConnection.getInputStream() : socket.getInputStream();
		@SuppressWarnings("resource")
		BufferedInputStream reader = new BufferedInputStream(in);
		int n;
		byte[] buff = new byte[BUFF_SIZE];
		while((n = reader.read(buff)) != -1) {
			responseStr.append(new String(Arrays.copyOf(buff, n)));
			copyFromBuffer(byteInfo, buff, n);
		}
	}
	
	private synchronized void handleCookies(HttpResponse response) throws HttpHelperException {
		boolean cookiesChanged = false;
		for (String newCookie : response.getHeaderValues("set-cookie")) {
			cookiesChanged = true;
			Cookie cookie = new Cookie(newCookie);
			cookies.remove(cookie);
			cookies.add(cookie);
		}
		if (cookiesChanged && storeCookies) {
			CookiesStore.store(cookies, cookiesPath);
		}
	}
	
	private void copyFromBuffer(ByteInfo byteInfo, byte[] src, int n) {
		while (byteInfo.size + n > byteInfo.maxSize) {
			byteInfo.maxSize *= 2;
			byteInfo.arr = Arrays.copyOf(byteInfo.arr, byteInfo.maxSize);
		}
		System.arraycopy(src, 0, byteInfo.arr, byteInfo.size, n);
		byteInfo.size += n;
	}
	
	public HttpHelper clone() {
		return clone(host, port);
	}
	
	public HttpHelper clone(String host) {
		return clone(host, port);
	}
	
	public HttpHelper clone(String host, int port) {
		try {
			return new HttpHelper(host, port, defaultVersion, defaultHost, keepAlive, 
					minDelay, maxDelay, new ArrayList<>(defaultHeaders), cookies, useHttps);
		} catch (HttpHelperException e) {
			throw new IllegalStateException("Socket should be openable", e);
		}
	}
	
	private static String getLocation(HttpRequest req, HttpResponse resp) {
		String loc = resp.getHeaderValue("location").get();
		if (!loc.startsWith("/")) {
			String aux[] = req.getPath().split("/");
			aux = Arrays.copyOfRange(aux, 0, aux.length - 1);
			String path = String.join("/", Arrays.asList(aux));
			return path + "/" + loc;
		}
		return loc.startsWith("/") ? loc : "/" + loc;
	}
	
	private class ByteInfo {
		byte[] arr;
		int maxSize;
		int size;
		
		public ByteInfo(byte[] arr) {
			super();
			this.arr = arr;
			this.maxSize = arr.length;
			this.size = 0;
		}
	}
}
