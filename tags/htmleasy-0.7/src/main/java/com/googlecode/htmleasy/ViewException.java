package com.googlecode.htmleasy;

import javax.ejb.ApplicationException;

/**
 * If thrown, this triggers rendering the specified view.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
@SuppressWarnings("serial")
@ApplicationException(rollback=false)
public class ViewException extends RuntimeException
{
	protected Viewable view;
	
	/** */
	public ViewException(Viewable view)
	{
		super(view.toString());
		
		this.view = view;
	}
	
	/** */
	public Viewable getView() { return this.view; }
}
