package com.googlecode.htmleasy.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.InternalServerErrorException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.googlecode.htmleasy.View;
import com.googlecode.htmleasy.ViewAndModel;
import com.googlecode.htmleasy.ViewWith;

/**
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
@SuppressWarnings("unchecked")
@Provider
@Produces("text/html")
public class HtmlWriter implements MessageBodyWriter
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(HtmlWriter.class.getName());

	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#getSize(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	//@Override
	public long getSize(Object obj, Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		// No chance of figuring this out ahead of time
		return -1;
	}

	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#isWriteable(java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
	 */
	//@Override
	public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		if (View.class.isAssignableFrom(type))
			return true;
		else
			return this.getViewWith(type, annotations) != null;
	}

	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.MessageBodyWriter#writeTo(java.lang.Object, java.lang.Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap, java.io.OutputStream)
	 */
	//@Override
	public void writeTo(Object obj, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
	{
		String path = null;
		Object model = null;
		String modelName = null;
		
		if (obj instanceof View)
		{
			path = ((View)obj).getPath();
			
			if (obj instanceof ViewAndModel)
			{
				model = ((ViewAndModel)obj).getModel();
				modelName = ((ViewAndModel)obj).getModelName();
			}
		}
		else
		{
			// Since isWriteable should have been called, this should never be null
			ViewWith viewWith = this.getViewWith(type, annotations);
			if (viewWith == null)
				throw new InternalServerErrorException("No " + ViewWith.class.getSimpleName() + " annotation found for object of type " + type.getName());
			
			path = viewWith.value();
			model = obj;
			modelName = viewWith.modelName();
		}
		
		this.forward(path, model, modelName);
	}
	
	/**
	 * Does the work of a forward.
	 * 
	 * @param path is the path to dispatch to
	 * @param model is the object to place at modelName in the request attrs.
	 * @param modelName is the name in the request attrs to set the model object, or null for "don't"
	 */
	protected void forward(String path, Object model, String modelName) throws IOException, WebApplicationException
	{
		HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
		
		RequestDispatcher disp = request.getRequestDispatcher(path);
		if (disp == null)
			throw new InternalServerErrorException("No dispatcher found for path '" + path + "'");
		
		HttpServletResponse response = ResteasyProviderFactory.getContextData(HttpServletResponse.class);

		if (modelName != null)
			request.setAttribute(modelName, model);
		
		try
		{
			disp.forward(request, response);
		}
		catch (ServletException ex)
		{
			throw new WebApplicationException(ex);
		}
	}

	/** 
	 * @return the relevant view annotation, or null if no view can be determined
	 */
	protected ViewWith getViewWith(Class type, Annotation[] methodAnnotations)
	{
		if (methodAnnotations != null)
		{
			for (Annotation anno: methodAnnotations)
			{
				if (anno instanceof ViewWith)
				{
					Class forClass = ((ViewWith)anno).ifClass();
					// Note that View.class is a sentinel value indicating "all classes"
					if (ViewWith.class.equals(forClass) || forClass.isAssignableFrom(type))
					{
						return (ViewWith)anno;
					}
				}
			}
		}
		
		return (ViewWith)type.getAnnotation(ViewWith.class);
	}
}
