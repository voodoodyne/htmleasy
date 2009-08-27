package com.googlecode.htmleasy;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

import org.jboss.resteasy.spi.InternalServerErrorException;


/**
 * The Htmleasy delegates to this class for actual view rendering.  If you
 * return one of these from a resource method, Htmleasy will render it.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class View implements Viewable
{
	/** If left unspecified, the default name of the model in the request attributes */
	public static final String DEFAULT_MODEL_NAME = "model";
	
	/** */
	protected String path;
	protected Object model;
	protected String modelName;
	
	/**
	 * Creates a view without a model.
	 * 
	 * @param path will be dispatched to using the servlet container; it should
	 *  have a leading /.
	 */
	public View(String path)
	{
		this(path, null, null);
	}
	
	/** */
	public View(String path, Object model)
	{
		this(path, model, DEFAULT_MODEL_NAME);
	}

	/** */
	public View(String path, Object model, String modelName)
	{
		this.path = path;
		this.model = model;
		this.modelName = modelName;
	}

	/** */
	public String getPath() { return this.path; }
	public Object getModel() { return this.model; }
	public String getModelName() { return this.modelName; }

	/**
	 * Sets up the model in the request attributes, creates a dispatcher, and forwards the request.
	 */
	public void render(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException, WebApplicationException
	{
		if (this.modelName != null)
			request.setAttribute(this.modelName, this.model);
		
		RequestDispatcher disp = request.getRequestDispatcher(path);
		if (disp == null)
			throw new InternalServerErrorException("No dispatcher found for path '" + path + "'");
		
		disp.forward(request, response);
	}
}
