package com.googlecode.htmleasy.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Useful utility which logs all calls to the HttpServletResponse methods
 * for debugging purposes.  Dynamic proxies don't work with concrete classes.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class LoggingHttpServletResponseWrapper extends HttpServletResponseWrapper
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(LoggingHttpServletResponseWrapper.class);
	
	/** */
	public LoggingHttpServletResponseWrapper(HttpServletResponse response)
	{
		super(response);
	}

	@Override
	public void addCookie(Cookie cookie)
	{
		log.debug("addCookie({})", cookie);
		super.addCookie(cookie);
	}

	@Override
	public void addDateHeader(String name, long date)
	{
		log.debug("addDateHeader({}, {})", name, date);
		super.addDateHeader(name, date);
	}

	@Override
	public void addHeader(String name, String value)
	{
		log.debug("addHeader({}, {})", name, value);
		super.addHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value)
	{
		log.debug("addIntHeader({}, {})", name, value);
		super.addIntHeader(name, value);
	}

	@Override
	public boolean containsHeader(String name)
	{
		log.debug("containsHeader({})", name);
		return super.containsHeader(name);
	}

	@Override
	public String encodeRedirectUrl(String url)
	{
		log.debug("encodeRedirectUrl({})", url);
		return super.encodeRedirectUrl(url);
	}

	@Override
	public String encodeRedirectURL(String url)
	{
		log.debug("encodeRedirectURL({})", url);
		return super.encodeRedirectURL(url);
	}

	@Override
	public String encodeUrl(String url)
	{
		log.debug("encodeUrl({})", url);
		return super.encodeUrl(url);
	}

	@Override
	public String encodeURL(String url)
	{
		log.debug("encodeURL({})", url);
		return super.encodeURL(url);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException
	{
		log.debug("sendError({}, {})", sc, msg);
		super.sendError(sc, msg);
	}

	@Override
	public void sendError(int sc) throws IOException
	{
		log.debug("sendError({})", sc);
		super.sendError(sc);
	}

	@Override
	public void sendRedirect(String location) throws IOException
	{
		log.debug("sendRedirect({})", location);
		super.sendRedirect(location);
	}

	@Override
	public void setDateHeader(String name, long date)
	{
		log.debug("setDateHeader({}, {})", name, date);
		super.setDateHeader(name, date);
	}

	@Override
	public void setHeader(String name, String value)
	{
		log.debug("setHeader({}, {})", name, value);
		super.setHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value)
	{
		log.debug("setIntHeader({}, {})", name, value);
		super.setIntHeader(name, value);
	}

	@Override
	public void setStatus(int sc, String sm)
	{
		log.debug("setStatus({}, {})", sc, sm);
		super.setStatus(sc, sm);
	}

	@Override
	public void setStatus(int sc)
	{
		log.debug("setStatus({})", sc);
		super.setStatus(sc);
	}

	@Override
	public void flushBuffer() throws IOException
	{
		log.debug("flushBuffer()");
		super.flushBuffer();
	}

	@Override
	public int getBufferSize()
	{
		log.debug("getBufferSize()");
		return super.getBufferSize();
	}

	@Override
	public String getCharacterEncoding()
	{
		log.debug("getCharacterEncoding()");
		return super.getCharacterEncoding();
	}

	@Override
	public String getContentType()
	{
		log.debug("getContentType()");
		return super.getContentType();
	}

	@Override
	public Locale getLocale()
	{
		log.debug("getLocale()");
		return super.getLocale();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException
	{
		log.debug("getOutputStream()");
		return super.getOutputStream();
	}

	@Override
	public ServletResponse getResponse()
	{
		log.debug("getResponse()");
		return super.getResponse();
	}

	@Override
	public PrintWriter getWriter() throws IOException
	{
		log.debug("getWriter()");
		return super.getWriter();
	}

	@Override
	public boolean isCommitted()
	{
		log.debug("isCommitted()");
		return super.isCommitted();
	}

	@Override
	public void reset()
	{
		log.debug("reset()");
		super.reset();
	}

	@Override
	public void resetBuffer()
	{
		log.debug("resetBuffer()");
		super.resetBuffer();
	}

	@Override
	public void setBufferSize(int size)
	{
		log.debug("setBufferSize({})", size);
		super.setBufferSize(size);
	}

	@Override
	public void setCharacterEncoding(String charset)
	{
		log.debug("setCharacterEncoding({})", charset);
		super.setCharacterEncoding(charset);
	}

	@Override
	public void setContentLength(int len)
	{
		log.debug("setContentLength({})", len);
		super.setContentLength(len);
	}

	@Override
	public void setContentType(String type)
	{
		log.debug("setContentType({})", type);
		super.setContentType(type);
	}

	@Override
	public void setLocale(Locale loc)
	{
		log.debug("setLocale({})", loc);
		super.setLocale(loc);
	}

	@Override
	public void setResponse(ServletResponse response)
	{
		log.debug("setResponse({})", response);
		super.setResponse(response);
	}

}
