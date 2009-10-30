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
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.htmleasy.util.LoggingHttpServletResponseWrapper;

/**
 * <p>
 * This filter allows Resteasy resources to share the URL space with
 * normal (jpg, jsp, etc) resources. That is, Resteasy can serve /foo and the
 * container can serve /my.jpg.  It also "fixes" containers that take
 * the servlet spec too literally.</p>
 * 
 * <p>The servlet spec says that you can only call ServletResponse.getWriter()
 * *or* ServletResponse.getOutputStream() within a request, and the second
 * call should generate an exception.  This is asinine and makes dispatching
 * (forwards and includes) nearly impossible.  Resteasy calls getOutputStream()
 * internally; if during the context of a Resteasy call you forward to a JSP
 * template, the JSP engine is likely to call getWriter().  You can do this on
 * Tomcat (or maybe Tomcat's JSP engine calls getOutputStream()) but it utterly
 * fails on Jetty (as of 2009-10-28).  The solution:  the wrapper for this
 * filter transmografies getWriter() into getOutputStream().
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
 * This is the suggested mapping for this filter in {@code web.xml}.
 * </p>
 * 
 * <pre>
 *  &lt;filter&gt;
 *      &lt;description&gt;Maps requests to Resteasy only if they are claimed by Resteasy, all others are serviced by the container normally.&lt;/description&gt;
 *      &lt;display-name&gt;Resteasy Filter&lt;/display-name&gt;
 *      &lt;filter-name&gt;ResteasyFilter&lt;/filter-name&gt;
 *      &lt;filter-class&gt;com.googlecode.htmleasy.ResteasyFilter&lt;/filter-class&gt;
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
 * <p>This code was inspired by Stripes' DynamicMappingFilter, but our code is far far
 * simpler because we check with Resteasy first, *then* send to the container:
 * http://stripes.svn.sourceforge.net/svnroot/stripes/tags/1.5.1/stripes/src/net/sourceforge/stripes/controller/DynamicMappingFilter.java</p>
 * 
 * @author Jeff Schnitzer
 */
public class ResteasyFilter implements Filter
{
	/** */
	private final static Logger log = LoggerFactory.getLogger(ResteasyFilter.class);
	
	/** Good for shunting output to the bitbucket */
	static final ServletOutputStream NOOP_OUTPUTSTREAM = new ServletOutputStream() {
		@Override public void write(int b) throws IOException {}
		@Override public void write(byte[] b, int off, int len) throws IOException {}
	};
	
	class LoggingServletOutputStream extends ServletOutputStream
	{
		ServletOutputStream base;
		public LoggingServletOutputStream(ServletOutputStream wrap)
		{
			this.base = wrap;
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException
		{
			log.debug("writing: " + new String(b, off, len));
			this.base.write(b, off, len);
		}

		@Override
		public void write(byte[] b) throws IOException
		{
			log.debug("writing: " + new String(b));
			this.base.write(b);
		}

		@Override
		public void write(int b) throws IOException
		{
			log.debug("writing: " + (char)b);
			this.base.write(b);
		}
		
	}
	
	/**
	 * <p>An {@link HttpServletResponseWrapper} that traps HTTP errors by
	 * overriding {@code sendError(int, ..)}.  If the error is 404,
	 * anything written is abandoned.  If the response is anything else,
	 * it is passed through as-is.</p>
	 * 
	 * <p>Note that this wrapper translates getWriter() calls into
	 * getOutputStream() calls to work around broken containers (and
	 * broken specs).
	 */
	public class ShuntingResponseWrapper extends HttpServletResponseWrapper
	{
		/** True when we get a 404 */
		boolean shunted;
		
		/** Wrap the given {@code response}. */
		public ShuntingResponseWrapper(HttpServletResponse response)
		{
			super(response);
		}

		/** */
		@Override
		public void sendError(int errorCode, String errorMessage) throws IOException
		{
			if (errorCode == HttpServletResponse.SC_NOT_FOUND)
			{
				log.debug("Got 404, going to shunted mode");
				this.shunted = true;
			}
			else
				super.sendError(errorCode, errorMessage);
		}

		/** */
		@Override
		public void sendError(int errorCode) throws IOException
		{
			if (errorCode == HttpServletResponse.SC_NOT_FOUND)
			{
				log.debug("Got 404, going to shunted mode");
				this.shunted = true;
			}
			else
				super.sendError(errorCode);
		}

		/** This actually translates the call to getOutputStream() */
		@Override
		public PrintWriter getWriter() throws IOException
		{
			return new PrintWriter(this.getOutputStream());
			//return super.getWriter();
		}

		/** */
		@Override
		public ServletOutputStream getOutputStream() throws IOException
		{
			if (log.isDebugEnabled())
				log.debug("Getting a " + ((this.shunted)?"NOOP":"real")  + " OutputStream");
			
			if (this.shunted)
				return NOOP_OUTPUTSTREAM;
			else
				return new LoggingServletOutputStream(super.getOutputStream());
		}

		/**
		 * 
		 */
		public boolean isShunted() { return this.shunted; }
	}

	/** */
	private HttpServletDispatcher resteasyServlet;

	/**
	 */
	public void init(final FilterConfig config) throws ServletException
	{
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

	/**
	 */
	public void destroy()
	{
		this.resteasyServlet.destroy();
	}

	/**
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		ShuntingResponseWrapper wrapper = new ShuntingResponseWrapper((HttpServletResponse) response);

		log.debug("############### CHECKING WITH RESTEASY");
		this.resteasyServlet.service(request, new LoggingHttpServletResponseWrapper(wrapper));

		// If we were shunted, continue with the filter, otherwise we're done
		if (wrapper.isShunted())
		{
			log.debug("############### GOING TO CONTAINER");
			response.reset();
			chain.doFilter(request, response);
		}
	}
}