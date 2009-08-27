package com.googlecode.htmleasy.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

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
import com.googlecode.htmleasy.ViewWith;
import com.googlecode.htmleasy.Viewable;

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
		if (Viewable.class.isAssignableFrom(type))
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
		Viewable viewingPleasure;
		
		if (obj instanceof Viewable)
		{
			viewingPleasure = (Viewable)obj;
		}
		else
		{
			// Since isWriteable should have been called, this should never be null
			ViewWith viewWith = this.getViewWith(type, annotations);
			if (viewWith == null)
				throw new InternalServerErrorException("No " + ViewWith.class.getSimpleName() + " annotation found for object of type " + type.getName());
			
			viewingPleasure = new View(viewWith.value(), obj, viewWith.modelName());
		}
		
		HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
		HttpServletResponse response = ResteasyProviderFactory.getContextData(HttpServletResponse.class);
		
		try
		{
			viewingPleasure.render(request, response);
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
