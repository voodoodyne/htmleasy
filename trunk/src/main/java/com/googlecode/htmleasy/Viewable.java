package com.googlecode.htmleasy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

/**
 * If you return one of these from a resource method, Htmleasy will render it.  This
 * interface allows for exotic view rendering types.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public interface Viewable
{
	/**
	 * Called to do the actual work of rendering a view.  Note that while ServletException
	 * can be thrown, WebApplicationException is preferred.
	 */
	public void render(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException, WebApplicationException;
}
