package com.googlecode.htmleasy;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.ApplicationException;

/**
 * If thrown, this triggers a redirect to the specified path.  The path must be
 * a valid URI and should begin with '/'.  The webapp context path will automatically
 * be prepended, if appropriate.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
@SuppressWarnings("serial")
@ApplicationException(rollback=false)
public class RedirectException extends RuntimeException
{
	protected URI path;
	
	public RedirectException(URI path)
	{
		super(path.toString());
		
		this.path = path;
	}
	
	/** @param path must be a valid URI */
	public RedirectException(String path)
	{
		super(path);
		
		try
		{
			this.path = new URI(path);
		}
		catch (URISyntaxException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public URI getPath()
	{
		return this.path;
	}
}
