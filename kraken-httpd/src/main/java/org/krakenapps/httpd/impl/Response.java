/*
 * Copyright 2011 Future Systems
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krakenapps.httpd.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response implements HttpServletResponse {
	private final Logger logger = LoggerFactory.getLogger(Response.class.getName());

	private BundleContext bc;
	private ChannelHandlerContext ctx;
	private HttpRequest req;
	private ServletOutputStream os;
	private PrintWriter writer;
	private HttpResponseStatus status = HttpResponseStatus.OK;
	private Map<String, Object> headers = new HashMap<String, Object>();
	private Set<Cookie> cookies = new HashSet<Cookie>();

	public Response(BundleContext bc, ChannelHandlerContext ctx, HttpRequest req) {
		this.bc = bc;
		this.ctx = ctx;
		this.req = req;
		this.os = new ResponseOutputStream();
		this.writer = new PrintWriter(os);
	}

	private class ResponseOutputStream extends ServletOutputStream {
		private boolean closed = false;
		private ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

		@Override
		public void write(int b) throws IOException {
			buf.writeByte(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			write(b, 0, b.length);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			buf.writeBytes(b, off, len);
		}

		@Override
		public void close() throws IOException {
			if (closed)
				return;

			closed = true;

			// send response if not sent
			HttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);

			String cookie = null;
			for (Cookie c : cookies) {
				if (cookie == null)
					cookie = String.format("%s=%s", c.getName(), c.getValue());
				else
					cookie = String.format("%s; %s=%s", cookie, c.getName(), c.getValue());
			}

			if (cookie != null)
				headers.put(HttpHeaders.Names.COOKIE, cookie);

			for (String name : headers.keySet())
				resp.setHeader(name, headers.get(name));

			logger.debug("kraken webconsole: flush response [{}]", buf.readableBytes());

			ChannelBuffer dup = buf.duplicate();
			buf.clear();

			resp.setContent(dup.duplicate());
			HttpHeaders.setContentLength(resp, dup.readableBytes());

			ChannelFuture f = ctx.getChannel().write(resp);
			if (!HttpHeaders.isKeepAlive(req))
				f.addListener(ChannelFutureListener.CLOSE);
		}

		@Override
		public void flush() throws IOException {
			// do not move flush code here (header should not sent twice)
		}

	}

	@Override
	public String getCharacterEncoding() {
		String contentType = (String) headers.get(HttpHeaders.Names.CONTENT_TYPE);
		if (contentType == null || !contentType.contains("charset"))
			return null;
		for (String t : contentType.split(";")) {
			if (t.trim().startsWith("charset"))
				return t.split("=")[1].trim();
		}
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return os;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return writer;
	}

	@Override
	public void setContentLength(int len) {
		headers.put(HttpHeaders.Names.CONTENT_LENGTH, len);
	}

	@Override
	public void setContentType(String type) {
		headers.put(HttpHeaders.Names.CONTENT_TYPE, type);
	}

	@Override
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.containsKey(name);
	}

	@Override
	public String encodeRedirectURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	@Override
	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}

	@Override
	public String encodeURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	@Override
	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/html");
		this.status = HttpResponseStatus.valueOf(sc);
		if (msg == null)
			msg = "";

		String body = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" //
				+ "<html><head><title>" + sc + " "
				+ status.getReasonPhrase()
				+ "</title></head>\n" //
				+ "<body><h1>" + sc + " " + status.getReasonPhrase() + "</h1><pre>"
				+ msg
				+ "</pre><hr/><address>Kraken Web Console/"
				+ bc.getBundle().getHeaders().get(Constants.BUNDLE_VERSION)
				+ "</address></body></html>";

		writer.append(body);
		writer.close();
	}

	@Override
	public void sendError(int sc) throws IOException {
		sendError(sc, null);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		this.status = HttpResponseStatus.MOVED_PERMANENTLY;
		setHeader(HttpHeaders.Names.CONTENT_TYPE, "text/html");
		setHeader(HttpHeaders.Names.LOCATION, location);
	}

	@Override
	public void setDateHeader(String name, long date) {
		headers.put(name, date);
	}

	@Override
	public void setHeader(String name, String value) {
		headers.put(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		headers.put(name, value);
	}

	@Deprecated
	@Override
	public void setStatus(int sc, String sm) {
		setStatus(sc);
	}

	@Override
	public void setStatus(int sc) {
		this.status = HttpResponseStatus.valueOf(sc);
	}

	public void close() {
		writer.close();
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBufferSize(int size) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocale(Locale loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addDateHeader(String name, long date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addHeader(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addIntHeader(String name, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}
}
