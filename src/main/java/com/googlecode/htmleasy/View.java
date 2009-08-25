package com.googlecode.htmleasy;


/**
 * A wrapper class for a path which the HtmlWriter will recognize and use to
 * render a specific view.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
public class View
{
	protected String path;
	
	/**
	 * @param path will be dispatched to using the servlet container; it should
	 *  have a leading /
	 */
	public View(String path)
	{
		this.path = path;
	}
	
	public String getPath() { return this.path; }
}
