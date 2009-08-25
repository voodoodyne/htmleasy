package com.googlecode.htmleasy;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation which defines a view for an Htmleasy model.  Place this annotation
 * on a data class or a resource method that returns a data class.  Method
 * annotation overrides the class annotation.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface ViewWith
{
	/** The view to forward to; something like /render_stuff.jsp */
	String value();
	
	/**
	 * If this is on a method, you can specify multiple views depending on the actual 
	 * returned class of the model.  Note that View.class is a sentinel value for "all",
	 * needed because Java won't allow null as a default.
	 */
	@SuppressWarnings("unchecked")
	Class ifClass() default ViewWith.class;
	
	/** The name of the model in the request attributes */
	String modelName() default "model";
}
