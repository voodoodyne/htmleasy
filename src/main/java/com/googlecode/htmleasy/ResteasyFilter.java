/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.htmleasy;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This is a modified version of Stripes' DynamicMappingFilter, adapted for use
 * with Resteasy. It allows Resteasy resources to share the URL space with
 * normal (jpg, jsp, etc) resources. That is, Resteasy can serve /foo and the
 * container can serve /my.jpg.</p>
 * 
 * <p>Note that this filter may become obsolete if and when Resteasy is
 * converted from a Servlet to a Filter itself; until then this will suffice.</p>
 * 
 * <p>The way this works:</p>
 * <ol>
 * <li>Map this filter to /* in your web.xml.</li>
 * <li>This filter creates an internal version of the Resteasy servlet. You do
 * not need to define the Resteasy servlet in web.xml.</li>
 * <li>When a request comes in, this filter first dispatches it to the Resteasy
 * servlet.  If Resteasy handles the request, great.</li>
 * <li>If Resteasy returns 404, the filter dipatches the request down the filter
 * chain to the container as normal.</li>
 * </ol>
 * <p>
 * Note that this filter <em>MUST</em> be the last filter in the filter chain.
 * When dispatching the request to Resteasy, no subsequent filters will be run.
 * </p>
 * <p>
 * This filter accepts one init-param. {@code IncludeBufferSize} (optional,
 * default 1024) sets the number of characters to be buffered by
 * {@link TempBufferWriter} for include requests. See {@link TempBufferWriter}
 * for more information.
 * <p>
 * This is the suggested mapping for this filter in {@code web.xml}.
 * </p>
 * 
 * <pre>
 *  &lt;filter&gt;
 *      &lt;description&gt;Maps requests to Resteasy only if they are claimed by Resteasy, all others are serviced by the container normally.&lt;/description&gt;
 *      &lt;display-name&gt;Resteasy Filter&lt;/display-name&gt;
 *      &lt;filter-name&gt;ResteasyFilter&lt;/filter-name&gt;
 *      &lt;filter-class&gt;
 *          test.ResteasyFilter
 *      &lt;/filter-class&gt;
 *  &lt;/filter&gt;
 *  
 *  &lt;filter-mapping&gt;
 *      &lt;filter-name&gt;ResteasyFilter&lt;/filter-name&gt;
 *      &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *      &lt;dispatcher&gt;REQUEST&lt;/dispatcher&gt;
 *      &lt;dispatcher&gt;FORWARD&lt;/dispatcher&gt;
 *      &lt;dispatcher&gt;INCLUDE&lt;/dispatcher&gt;
 *  &lt;/filter-mapping&gt;
 * </pre>
 * 
 * <p>The original code is here:
 * http://stripes.svn.sourceforge.net/svnroot/stripes/tags/1.5.1/stripes/src/net/sourceforge/stripes/controller/DynamicMappingFilter.java</p>
 * 
 * @author Ben Gunter
 * @author Jeff Schnitzer
 */
public class ResteasyFilter implements Filter
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(ResteasyFilter.class);
	
	/**
	 * The name of the init-param that can be used to set the size of the buffer
	 * used by {@link TempBufferWriter} before it overflows.
	 */
	private static final String INCLUDE_BUFFER_SIZE_PARAM = "IncludeBufferSize";

	/**
	 * <p>
	 * A {@link Writer} that passes characters to a {@link PrintWriter}. It
	 * buffers the first {@code N} characters written to it and automatically
	 * overflows when the number of characters written exceeds the limit. The
	 * size of the buffer defaults to 1024 characters, but it can be changed
	 * using the {@code IncludeBufferSize} filter init-param in {@code web.xml}.
	 * If {@code IncludeBufferSize} is zero or negative, then a
	 * {@link TempBufferWriter} will not be used at all. This is only a good
	 * idea if your servlet container does not write an error message to output
	 * when it can't find an included resource or if you only include resources
	 * that do not depend on this filter to be delivered, such as other
	 * servlets, JSPs, static resources, ActionBeans that are mapped with a
	 * prefix ({@code /action/*}) or suffix ({@code *.action}), etc.
	 * </p>
	 * <p>
	 * This writer is used to partially buffer the output of includes. Some
	 * (all?) servlet containers write a message to the output stream indicating
	 * if an included resource is missing because if the response has already
	 * been committed, they cannot send a 404 error. Since the filter depends on
	 * getting a 404 before it attempts to dispatch an {@code ActionBean}, that
	 * is problematic. So in using this writer, we assume that the length of the
	 * "missing resource" message will be less than the buffer size and we
	 * discard that message if we're able to map the included URL to an {@code
	 * ActionBean}. If there is no 404 then the output will be sent normally. If
	 * there is a 404 and the URL does not match an ActionBean then the "missing
	 * resource" message is sent through.
	 * </p>
	 * 
	 * @author Ben Gunter
	 */
	public class TempBufferWriter extends Writer
	{
		private StringWriter buffer;
		private PrintWriter out;

		public TempBufferWriter(PrintWriter out)
		{
			this.out = out;
			this.buffer = new StringWriter(includeBufferSize);
		}

		@Override
		public void close() throws IOException
		{
			flush();
			out.close();
		}

		@Override
		public void flush() throws IOException
		{
			overflow();
			out.flush();
		}

		@Override
		public void write(char[] chars, int offset, int length) throws IOException
		{
			if (buffer == null)
			{
				out.write(chars, offset, length);
			}
			else if (buffer.getBuffer().length() + length > includeBufferSize)
			{
				overflow();
				out.write(chars, offset, length);
			}
			else
			{
				buffer.write(chars, offset, length);
			}
		}

		/**
		 * Write the contents of the buffer to the underlying writer. After a
		 * call to {@link #overflow()}, all future writes to this writer will
		 * pass directly to the underlying writer.
		 */
		protected void overflow()
		{
			if (buffer != null)
			{
				out.print(buffer.toString());
				buffer = null;
			}
		}
	}

	/**
	 * An {@link HttpServletResponseWrapper} that traps HTTP errors by
	 * overriding {@code sendError(int, ..)}. The error code can be retrieved by
	 * calling {@link #getErrorCode()}. A call to {@link #proceed()} sends the
	 * error to the client.
	 * 
	 * @author Ben Gunter
	 */
	public class ErrorTrappingResponseWrapper extends HttpServletResponseWrapper
	{
		private Integer errorCode;
		private String errorMessage;
		private boolean include;
		private PrintWriter printWriter;
		private TempBufferWriter tempBufferWriter;

		/** Wrap the given {@code response}. */
		public ErrorTrappingResponseWrapper(HttpServletResponse response)
		{
			super(response);
		}

		@Override
		public void sendError(int errorCode, String errorMessage) throws IOException
		{
			this.errorCode = errorCode;
			this.errorMessage = errorMessage;
		}

		@Override
		public void sendError(int errorCode) throws IOException
		{
			this.errorCode = errorCode;
			this.errorMessage = null;
		}

		@Override
		public PrintWriter getWriter() throws IOException
		{
			if (isInclude() && includeBufferSize > 0)
			{
				if (printWriter == null)
				{
					tempBufferWriter = new TempBufferWriter(super.getWriter());
					printWriter = new PrintWriter(tempBufferWriter);
				}
				return printWriter;
			}
			else
			{
				return super.getWriter();
			}
		}

		/** True if the currently executing request is an include. */
		public boolean isInclude()
		{
			return include;
		}

		/** Indicate if the currently executing request is an include. */
		public void setInclude(boolean include)
		{
			this.include = include;
		}

		/** Get the error code that was passed into {@code sendError(int, ..)} */
		public Integer getErrorCode()
		{
			return errorCode;
		}

		/** Clear error code and error message. */
		public void clearError()
		{
			this.errorCode = null;
			this.errorMessage = null;
		}

		/**
		 * Send the error, if any, to the client. If {@code sendError(int, ..)}
		 * has not previously been called, then do nothing.
		 */
		public void proceed() throws IOException
		{
			// Explicitly overflow the buffer so the output gets written
			if (tempBufferWriter != null)
				tempBufferWriter.overflow();

			if (errorCode != null)
			{
				if (errorMessage == null)
					super.sendError(errorCode);
				else
					super.sendError(errorCode, errorMessage);
			}
		}
	}

	/**
	 * The size of the buffer used by {@link TempBufferWriter} before it
	 * overflows.
	 */
	private int includeBufferSize = 1024;

	private HttpServletDispatcher resteasyServlet;

	public void init(final FilterConfig config) throws ServletException
	{
		try
		{
			this.includeBufferSize = Integer.valueOf(config.getInitParameter(INCLUDE_BUFFER_SIZE_PARAM).trim());
			log.info(this.getClass().getSimpleName() + " include buffer size is " + this.includeBufferSize);
		}
		catch (NullPointerException e)
		{
			// ignore it
		}
		catch (Exception e)
		{
			log.warn("Could not interpret '" +
					config.getInitParameter(INCLUDE_BUFFER_SIZE_PARAM) +
					"' as a number for init-param '" + INCLUDE_BUFFER_SIZE_PARAM +
					"'. Using default value of " + includeBufferSize + ".", e);
		}

		// Allow us to override the specific class for the servlet
		String servletClassName = (String)config.getInitParameter("resteasy.servlet.class");
		if (servletClassName == null)
		{
			this.resteasyServlet = new HttpServletDispatcher();
		}
		else
		{
			try
			{
				Class<? extends HttpServletDispatcher> servletClass =
					Class.forName(servletClassName).asSubclass(HttpServletDispatcher.class);
				
				this.resteasyServlet = servletClass.newInstance();
			}
			catch (Exception ex) { throw new ServletException(ex); }
		}
		
		this.resteasyServlet.init(new ServletConfig() {
			public String getInitParameter(String name)
			{
				return config.getInitParameter(name);
			}

			public Enumeration<?> getInitParameterNames()
			{
				return config.getInitParameterNames();
			}

			public ServletContext getServletContext()
			{
				return config.getServletContext();
			}

			public String getServletName()
			{
				return config.getFilterName();
			}
		});
	}

	public void destroy()
	{
		this.resteasyServlet.destroy();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		// Wrap the response in a wrapper that catches errors (but not exceptions)
		final ErrorTrappingResponseWrapper wrapper = new ErrorTrappingResponseWrapper((HttpServletResponse) response);
		wrapper.setInclude(request.getAttribute("javax.servlet.include.servlet_path") != null);

		this.resteasyServlet.service(request, wrapper);

		// If a SC_NOT_FOUND error occurred, then process through the chain normally
		Integer errorCode = wrapper.getErrorCode();
		if (errorCode != null && errorCode == HttpServletResponse.SC_NOT_FOUND)
		{
			chain.doFilter(request, response);
		}
		else
		{
			wrapper.proceed();
		}
	}
}