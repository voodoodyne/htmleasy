package com.googlecode.htmleasy.provider;

import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.googlecode.htmleasy.RedirectException;

/**
 * This mapper allows us to use RedirectException to issue, uh, redirects.
 * 
 * @author Jeff Schnitzer <jeff@infohazard.org>
 */
@Provider
public class RedirectExceptionMapper implements ExceptionMapper<RedirectException>
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(RedirectExceptionMapper.class.getName());

	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	//@Override
	public Response toResponse(RedirectException ex)
	{
		return Response.status(ex.getStatus()).contentLocation(ex.getPath()).build();
	}

}
