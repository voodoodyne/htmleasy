package com.googlecode.htmleasy;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation which defines a set of view definitions (ViewWith) items for an Htmleasy model.  Place this annotation
 * on a method and populate it an array of ViewWith annotations.  This allows the one method/controller to return
 * multiple types/models and the most appropriate view is selected to render the item.
 * 
 * e.g.
 * 
 *   <code>
 *   @ViewSet({@ViewWith(ifClass=Boat.class, value="/boat.jsp"), @ViewWith(ifClass=Car.class, value="/car.jsp")})
 *   </code>
 *   
 * Note: Order is important. The first matching view in the list is used.
 * 
 * @author Chris Dance <chris.dance@papercut.com>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface ViewSet
{
	/** The list of ViewWith items */
	ViewWith[] value();
}
