package com.googlecode.htmleasy;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.UriBuilder;

/**
 * If thrown, this triggers a redirect to the specified path. The path must be a
 * valid URI and should begin with '/'. The webapp context path will
 * automatically be prepended, if appropriate.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 * @author Chris Dance <chris.dance@papercut.com>
 */
@SuppressWarnings("serial")
@ApplicationException(rollback = false)
public class RedirectException extends RuntimeException
{
	protected URI path;

	public RedirectException(URI path)
	{
		super(path.toString());
		
		this.path = path;
	}

	/**
	 * @param path
	 *            must be a valid URI
	 */
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

	/**
	 * @param clazz
	 *            An Path annotated class to redirect too.
	 */
	public RedirectException(Class<?> clazz)
	{
		super(clazz.getName());

		try
		{
			this.path = UriBuilder.fromResource(clazz).build();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @param clazz
	 *            An Path annotated class.
	 * @param method
	 *            The Path annotated method to redirect too.
	 */
	public RedirectException(Class<?> clazz, String method)
	{
		super(clazz.getName());

		try
		{
			this.path = UriBuilder.fromResource(clazz).path(clazz, method).build();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public URI getPath()
	{
		return this.path;
	}
}
