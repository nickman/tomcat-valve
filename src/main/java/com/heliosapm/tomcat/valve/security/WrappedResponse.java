/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */
package com.heliosapm.tomcat.valve.security;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: WrappedResponse</p>
 * <p>Description: Wraps a catalina {@link Response} to validate cookies</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.tomcat.valve.security.WrappedResponse</code></p>
 */

public class WrappedResponse extends Response {
	/** The response to delegate to */
	protected final Response delegate;
	
	/** Instance logger */
	protected static final Logger log = LoggerFactory.getLogger(WrappedResponse.class);
	
	/**
	 * Wraps the passed response 
	 * @param delegate The delegate response to wrap
	 * @return the wrapped response
	 */
	public static Response wrap(final Response delegate) {
		if(delegate instanceof WrappedResponse) return delegate;
		return new WrappedResponse(delegate);
	}
	
	
	protected String render(final Cookie cookie) {
		if(cookie==null) return "Cookie: [null]";
		final StringBuilder b = new StringBuilder("Cookie: [");
		b.append("\n\tName:").append(cookie.getName());
		b.append("\n\tMaxAge:").append(cookie.getMaxAge());
		b.append("\n\tSecure:").append(cookie.getSecure());		
		if(cookie.getComment()!=null) b.append("\n\tComment:").append(cookie.getComment());
		if(cookie.getDomain()!=null) b.append("\n\tDomain:").append(cookie.getDomain());
		if(cookie.getPath()!=null) b.append("\n\tPath:").append(cookie.getPath());
		b.append("\n]");
		return b.toString();
	}
	
	/**
	 * Creates a new WrappedResponse
	 * @param delegate The response to delegate to
	 */
	public WrappedResponse(final Response delegate) {
		this.delegate = delegate;
	}

	/**
	 * Add the specified Cookie to those that will be included with this Response.
	 * @param cookie Cookie to be added
	 * @see org.apache.catalina.connector.Response#addCookie(javax.servlet.http.Cookie)
	 */
	@Override
	public void addCookie(final Cookie cookie) {
		log.info("Adding: {}", render(cookie));
		delegate.addCookie(cookie);
	}

	/**
	 * Add the specified Cookie to those that will be included with this Response.
	 * @param cookie Cookie to be added
	 * @param httpOnly true for an http only cookie
	 * @see org.apache.catalina.connector.Response#addCookieInternal(javax.servlet.http.Cookie, boolean)
	 */
	@Override
	public void addCookieInternal(final Cookie cookie, final boolean httpOnly) {
		log.info("Adding Internal: httpOnly [{}], {}", httpOnly, render(cookie));
		delegate.addCookieInternal(cookie, httpOnly);
	}

	/**
	 * Add the specified Cookie to those that will be included with this Response.
	 * @param cookie Cookie to be added
	 * @see org.apache.catalina.connector.Response#addCookieInternal(javax.servlet.http.Cookie)
	 */
	@Override
	public void addCookieInternal(final Cookie cookie) {
		log.info("Adding Internal: {}", render(cookie));
		delegate.addCookieInternal(cookie, true);
		delegate.getCookies();
	}

	/**
	 * Special method for adding a session cookie as we should be overriding any previous
	 * @param cookie The session cookie to be added
	 * @param httpOnly true for an http only cookie
	 * @see org.apache.catalina.connector.Response#addSessionCookieInternal(javax.servlet.http.Cookie, boolean)
	 */
	@Override
	public void addSessionCookieInternal(final Cookie cookie, final boolean httpOnly) {
		log.info("Adding Session Internal: httpOnly [{}], {}", true, render(cookie));
		delegate.addSessionCookieInternal(cookie, httpOnly);
	}

	
	
	//=================================================================
	
	
	/**
	 * @param name
	 * @param value
	 * @see org.apache.catalina.connector.Response#addDateHeader(java.lang.String, long)
	 */
	@Override
	public void addDateHeader(String name, long value) {
		delegate.addDateHeader(name, value);
	}

	/**
	 * @param name
	 * @param value
	 * @see org.apache.catalina.connector.Response#addHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void addHeader(String name, String value) {
		delegate.addHeader(name, value);
	}

	/**
	 * @param name
	 * @param value
	 * @see org.apache.catalina.connector.Response#addIntHeader(java.lang.String, int)
	 */
	@Override
	public void addIntHeader(String name, int value) {
		delegate.addIntHeader(name, value);
	}


	/**
	 * 
	 * @see org.apache.catalina.connector.Response#clearEncoders()
	 */
	@Override
	public void clearEncoders() {
		delegate.clearEncoders();
	}

	/**
	 * @param name
	 * @return
	 * @see org.apache.catalina.connector.Response#containsHeader(java.lang.String)
	 */
	@Override
	public boolean containsHeader(String name) {
		return delegate.containsHeader(name);
	}

	/**
	 * @return
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#createOutputStream()
	 */
	@Override
	public ServletOutputStream createOutputStream() throws IOException {
		return delegate.createOutputStream();
	}

	/**
	 * @param url
	 * @return
	 * @see org.apache.catalina.connector.Response#encodeRedirectURL(java.lang.String)
	 */
	@Override
	public String encodeRedirectURL(String url) {
		return delegate.encodeRedirectURL(url);
	}

	/**
	 * @param url
	 * @return
	 * @deprecated
	 * @see org.apache.catalina.connector.Response#encodeRedirectUrl(java.lang.String)
	 */
	@Override
	public String encodeRedirectUrl(String url) {
		return delegate.encodeRedirectUrl(url);
	}

	/**
	 * @param url
	 * @return
	 * @see org.apache.catalina.connector.Response#encodeURL(java.lang.String)
	 */
	@Override
	public String encodeURL(String url) {
		return delegate.encodeURL(url);
	}

	/**
	 * @param url
	 * @return
	 * @deprecated
	 * @see org.apache.catalina.connector.Response#encodeUrl(java.lang.String)
	 */
	@Override
	public String encodeUrl(String url) {
		return delegate.encodeUrl(url);
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	/**
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#finishResponse()
	 */
	@Override
	public void finishResponse() throws IOException {
		delegate.finishResponse();
	}

	/**
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#flushBuffer()
	 */
	@Override
	public void flushBuffer() throws IOException {
		delegate.flushBuffer();
	}

	/**
	 * @param cookie
	 * @param httpOnly
	 * @return
	 * @see org.apache.catalina.connector.Response#generateCookieString(javax.servlet.http.Cookie, boolean)
	 */
	@Override
	public StringBuffer generateCookieString(Cookie cookie, boolean httpOnly) {
		return delegate.generateCookieString(cookie, httpOnly);
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getBufferSize()
	 */
	@Override
	public int getBufferSize() {
		return delegate.getBufferSize();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getCharacterEncoding()
	 */
	@Override
	public String getCharacterEncoding() {
		return delegate.getCharacterEncoding();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getConnector()
	 */
	@Override
	public Connector getConnector() {
		return delegate.getConnector();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getContentCount()
	 */
	@Override
	public int getContentCount() {
		return delegate.getContentCount();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getContentCountLong()
	 */
	@Override
	public long getContentCountLong() {
		return delegate.getContentCountLong();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getContentLength()
	 */
	@Override
	public int getContentLength() {
		return delegate.getContentLength();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getContentType()
	 */
	@Override
	public String getContentType() {
		return delegate.getContentType();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getContext()
	 */
	@Override
	public Context getContext() {
		return delegate.getContext();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getCookies()
	 */
	@Override
	public Cookie[] getCookies() {
		return delegate.getCookies();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getCoyoteResponse()
	 */
	@Override
	public org.apache.coyote.Response getCoyoteResponse() {
		return delegate.getCoyoteResponse();
	}

	/**
	 * @param name
	 * @return
	 * @see org.apache.catalina.connector.Response#getHeader(java.lang.String)
	 */
	@Override
	public String getHeader(String name) {
		return delegate.getHeader(name);
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getHeaderNames()
	 */
	@Override
	public String[] getHeaderNames() {
		return delegate.getHeaderNames();
	}

	/**
	 * @param name
	 * @return
	 * @see org.apache.catalina.connector.Response#getHeaderValues(java.lang.String)
	 */
	@Override
	public String[] getHeaderValues(String name) {
		return delegate.getHeaderValues(name);
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getIncluded()
	 */
	@Override
	public boolean getIncluded() {
		return delegate.getIncluded();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getInfo()
	 */
	@Override
	public String getInfo() {
		return delegate.getInfo();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return delegate.getLocale();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getMessage()
	 */
	@Override
	public String getMessage() {
		return delegate.getMessage();
	}

	/**
	 * @return
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#getOutputStream()
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return delegate.getOutputStream();
	}

	/**
	 * @return
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#getReporter()
	 */
	@Override
	public PrintWriter getReporter() throws IOException {
		return delegate.getReporter();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getRequest()
	 */
	@Override
	public Request getRequest() {
		return delegate.getRequest();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getResponse()
	 */
	@Override
	public HttpServletResponse getResponse() {
		return delegate.getResponse();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getStatus()
	 */
	@Override
	public int getStatus() {
		return delegate.getStatus();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#getStream()
	 */
	@Override
	public OutputStream getStream() {
		return delegate.getStream();
	}

	/**
	 * @return
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#getWriter()
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		return delegate.getWriter();
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#isAppCommitted()
	 */
	@Override
	public boolean isAppCommitted() {
		return delegate.isAppCommitted();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return delegate.isClosed();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#isCommitted()
	 */
	@Override
	public boolean isCommitted() {
		return delegate.isCommitted();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#isError()
	 */
	@Override
	public boolean isError() {
		return delegate.isError();
	}

	/**
	 * @return
	 * @see org.apache.catalina.connector.Response#isSuspended()
	 */
	@Override
	public boolean isSuspended() {
		return delegate.isSuspended();
	}

	/**
	 * 
	 * @see org.apache.catalina.connector.Response#recycle()
	 */
	@Override
	public void recycle() {
		delegate.recycle();
	}

	/**
	 * 
	 * @see org.apache.catalina.connector.Response#reset()
	 */
	@Override
	public void reset() {
		delegate.reset();
	}

	/**
	 * @param status
	 * @param message
	 * @see org.apache.catalina.connector.Response#reset(int, java.lang.String)
	 */
	@Override
	public void reset(int status, String message) {
		delegate.reset(status, message);
	}

	/**
	 * 
	 * @see org.apache.catalina.connector.Response#resetBuffer()
	 */
	@Override
	public void resetBuffer() {
		delegate.resetBuffer();
	}

	/**
	 * @param resetWriterStreamFlags
	 * @see org.apache.catalina.connector.Response#resetBuffer(boolean)
	 */
	@Override
	public void resetBuffer(boolean resetWriterStreamFlags) {
		delegate.resetBuffer(resetWriterStreamFlags);
	}

	/**
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#sendAcknowledgement()
	 */
	@Override
	public void sendAcknowledgement() throws IOException {
		delegate.sendAcknowledgement();
	}

	/**
	 * @param status
	 * @param message
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#sendError(int, java.lang.String)
	 */
	@Override
	public void sendError(int status, String message) throws IOException {
		delegate.sendError(status, message);
	}

	/**
	 * @param status
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#sendError(int)
	 */
	@Override
	public void sendError(int status) throws IOException {
		delegate.sendError(status);
	}

	/**
	 * @param arg0
	 * @throws IOException
	 * @see org.apache.catalina.connector.Response#sendRedirect(java.lang.String)
	 */
	@Override
	public void sendRedirect(String arg0) throws IOException {
		delegate.sendRedirect(arg0);
	}

	/**
	 * @param appCommitted
	 * @see org.apache.catalina.connector.Response#setAppCommitted(boolean)
	 */
	@Override
	public void setAppCommitted(boolean appCommitted) {
		delegate.setAppCommitted(appCommitted);
	}

	/**
	 * @param size
	 * @see org.apache.catalina.connector.Response#setBufferSize(int)
	 */
	@Override
	public void setBufferSize(int size) {
		delegate.setBufferSize(size);
	}

	/**
	 * @param charset
	 * @see org.apache.catalina.connector.Response#setCharacterEncoding(java.lang.String)
	 */
	@Override
	public void setCharacterEncoding(String charset) {
		delegate.setCharacterEncoding(charset);
	}

	/**
	 * @param connector
	 * @see org.apache.catalina.connector.Response#setConnector(org.apache.catalina.connector.Connector)
	 */
	@Override
	public void setConnector(Connector connector) {
		delegate.setConnector(connector);
	}

	/**
	 * @param length
	 * @see org.apache.catalina.connector.Response#setContentLength(int)
	 */
	@Override
	public void setContentLength(int length) {
		delegate.setContentLength(length);
	}

	/**
	 * @param arg0
	 * @see org.apache.catalina.connector.Response#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String arg0) {
		delegate.setContentType(arg0);
	}

	/**
	 * @param context
	 * @see org.apache.catalina.connector.Response#setContext(org.apache.catalina.Context)
	 */
	@Override
	public void setContext(Context context) {
		delegate.setContext(context);
	}

	/**
	 * @param coyoteResponse
	 * @see org.apache.catalina.connector.Response#setCoyoteResponse(org.apache.coyote.Response)
	 */
	@Override
	public void setCoyoteResponse(org.apache.coyote.Response coyoteResponse) {
		delegate.setCoyoteResponse(coyoteResponse);
	}

	/**
	 * @param name
	 * @param value
	 * @see org.apache.catalina.connector.Response#setDateHeader(java.lang.String, long)
	 */
	@Override
	public void setDateHeader(String name, long value) {
		delegate.setDateHeader(name, value);
	}

	/**
	 * 
	 * @see org.apache.catalina.connector.Response#setError()
	 */
	@Override
	public void setError() {
		delegate.setError();
	}

	/**
	 * @param name
	 * @param value
	 * @see org.apache.catalina.connector.Response#setHeader(java.lang.String, java.lang.String)
	 */
	@Override
	public void setHeader(String name, String value) {
		delegate.setHeader(name, value);
	}

	/**
	 * @param included
	 * @see org.apache.catalina.connector.Response#setIncluded(boolean)
	 */
	@Override
	public void setIncluded(boolean included) {
		delegate.setIncluded(included);
	}

	/**
	 * @param name
	 * @param value
	 * @see org.apache.catalina.connector.Response#setIntHeader(java.lang.String, int)
	 */
	@Override
	public void setIntHeader(String name, int value) {
		delegate.setIntHeader(name, value);
	}

	/**
	 * @param locale
	 * @see org.apache.catalina.connector.Response#setLocale(java.util.Locale)
	 */
	@Override
	public void setLocale(Locale locale) {
		delegate.setLocale(locale);
	}

	/**
	 * @param request
	 * @see org.apache.catalina.connector.Response#setRequest(org.apache.catalina.connector.Request)
	 */
	@Override
	public void setRequest(Request request) {
		delegate.setRequest(request);
	}

	/**
	 * @param status
	 * @param message
	 * @deprecated
	 * @see org.apache.catalina.connector.Response#setStatus(int, java.lang.String)
	 */
	@Override
	public void setStatus(int status, String message) {
		delegate.setStatus(status, message);
	}

	/**
	 * @param status
	 * @see org.apache.catalina.connector.Response#setStatus(int)
	 */
	@Override
	public void setStatus(int status) {
		delegate.setStatus(status);
	}

	/**
	 * @param stream
	 * @see org.apache.catalina.connector.Response#setStream(java.io.OutputStream)
	 */
	@Override
	public void setStream(OutputStream stream) {
		delegate.setStream(stream);
	}

	/**
	 * @param suspended
	 * @see org.apache.catalina.connector.Response#setSuspended(boolean)
	 */
	@Override
	public void setSuspended(boolean suspended) {
		delegate.setSuspended(suspended);
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return delegate.toString();
	}

}
