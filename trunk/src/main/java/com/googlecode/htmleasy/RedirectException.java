package com.googlecode.htmleasy;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response.Status;
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
	protected Status status;	// 301, 302, 303

	/**
	 * Default status is SEE_OTHER (303) 
	 */
	public RedirectException(URI path)
	{
		this(Status.SEE_OTHER, path);
	}
	
	/**
	 * @param status must be SEE_OTHER or MOVED_PERMANENTLY
	 */
	public RedirectException(Status status, URI path)
	{
		super(path.toString());
		
		this.path = path;
		this.status = status;
	}

	/**
	 * Default status is SEE_OTHER (303) 
	 * @param path must be a valid URI
	 */
	public RedirectException(String path)
	{
		this(Status.SEE_OTHER, path);
	}
	
	/**
	 * @param status must be SEE_OTHER or MOVED_PERMANENTLY
	 * @param path must be a valid URI
	 */
	public RedirectException(Status status, String path)
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
		
		this.status = status;
	}

	/**
	 * Default status is SEE_OTHER (303) 
	 * @param clazz A Path annotated class to redirect too.
	 */
	public RedirectException(Class<?> clazz)
	{
		this(Status.SEE_OTHER, clazz);
	}
	
	/**
	 * @param status must be SEE_OTHER or MOVED_PERMANENTLY
	 * @param clazz A Path annotated class to redirect too.
	 */
	public RedirectException(Status status, Class<?> clazz)
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
		
		this.status = status;
	}

	/**
	 * Default status is SEE_OTHER (303) 
	 * @param clazz A Path annotated class.
	 * @param method The Path annotated method to redirect too.
	 */
	public RedirectException(Class<?> clazz, String method)
	{
		this(Status.SEE_OTHER, method);
	}
	
	/**
	 * @param status must be SEE_OTHER or MOVED_PERMANENTLY
	 * @param clazz A Path annotated class.
	 * @param method The Path annotated method to redirect too.
	 */
	public RedirectException(Status status, Class<?> clazz, String method)
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
		
		this.status = status;
	}

	public URI getPath()
	{
		return this.path;
	}
	
	public Status getStatus()
	{
		return this.status;
	}
}
