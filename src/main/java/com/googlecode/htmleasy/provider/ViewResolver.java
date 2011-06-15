/**
 * (c) 2010 Copyright by Vivian Steller <vivian@steller.info>. All rights reserved. 
 */
package com.googlecode.htmleasy.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.googlecode.htmleasy.View;
import com.googlecode.htmleasy.ViewWith;
import com.googlecode.htmleasy.Viewable;

/**
 * Resolves the view to be used for rendering a given object or type.
 * 
 * @author Vivian Steller
 * 
 */
public class ViewResolver
{
	public boolean isResolvable(Object object)
	{
		return isResolvable(object.getClass(), object.getClass().getGenericSuperclass(), null);
	}
	
	/**
	 * @return whether a view can be resolved for the given type
	 */
	public boolean isResolvable(Class<?> type, Type genericType, Annotation[] methodAnnotations)
	{
		if (Viewable.class.isAssignableFrom(type))
			return true;
		else
			return this.getViewWith(type, genericType, methodAnnotations) != null;
	}
	
	public Viewable getView(Object object)
	{
		if (object == null)
			return null;
		
		return getView(object, object.getClass(), object.getClass().getGenericSuperclass(), null);
	}
	
	/**
	 * @return the correct view to render object
	 */
	public Viewable getView(Object object, Class<?> type, Type genericType, Annotation[] annotations)
	{
		Viewable viewingPleasure;
		
		if (object instanceof Viewable)
		{
			viewingPleasure = (Viewable) object;
		}
		else
		{
			// Since isResolvable should have been called, this should never be null
			ViewWith viewWith = this.getViewWith(type, genericType, annotations);
			if (viewWith == null)
				return null;
			
			viewingPleasure = new View(viewWith.value(), object, viewWith.modelName());
		}
		return viewingPleasure;
	}
	
	/**
	 * @return the relevant view annotation, or null if no view can be determined
	 */
	protected ViewWith getViewWith(Class<?> type, Type genericType, Annotation[] methodAnnotations)
	{
		if (methodAnnotations != null)
		{
			for (Annotation anno : methodAnnotations)
			{
				if (anno instanceof ViewWith)
				{
					Class<?> forClass = ((ViewWith) anno).ifClass();
					// Note that View.class is a sentinel value indicating "all classes"
					if (ViewWith.class.equals(forClass) || forClass.isAssignableFrom(type))
					{
						return (ViewWith) anno;
					}
				}
			}
		}
		
		if (type != null && type.isAnnotationPresent(ViewWith.class))
		{
			return (ViewWith) type.getAnnotation(ViewWith.class);
		}
		
		if (genericType != null && genericType instanceof Class && ((Class<?>) genericType).isAnnotationPresent(ViewWith.class))
		{
			return (ViewWith) ((Class<?>) genericType).getAnnotation(ViewWith.class);
		}
		
		return null;
	}
	
}
