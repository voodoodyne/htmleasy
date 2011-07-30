/*
 * (c) Copyright 1999-2011 PaperCut Software Int. Pty. Ltd.
 * $Id$
 */
package com.googlecode.htmleasy;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.spi.HttpResponse;


/**
 * Special HTTP filter to support Htmleasy. To use, add something like this to your web.xml:
 * 
 *   <listener>
 *     <listener-class>
 *        org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap
 *     </listener-class>
 *   </listener>
 *
 *   <servlet>
 *     <servlet-name>Htmleasy</servlet-name>
 *     <servlet-class>
 *       com.googlecode.htmleasy.HtmleasyServletDispatcher
 *     </servlet-class>
 *   </servlet>
 *
 *   <servlet-mapping>
 *     <servlet-name>Htmleasy</servlet-name>
 *     <url-pattern>/mycontrollers/*</url-pattern>
 *   </servlet-mapping>
 *  
 * Because Htmleasy forwards through to another view (e.g. a JSP page) the
 * output stream must remain closed. This filter defers opening/creating the stream until a write is performed.
 * 
 * Implementation based on the HTMLServletDispatcher.java file found in the RestEasy SCM located at:
 * ~trunk/jaxrs/providers/resteasy-html/src/main/java/org/jboss/resteasy/plugins/providers/html/HtmlServletDispatcher.java
 * 
 * @author Chris Dance <chris.dance@papercut.com> 
 */
public class HtmleasyServletDispatcher extends HttpServletDispatcher {

    @Override
    protected HttpResponse createServletResponse(HttpServletResponse response) {
        return new HttpServletResponseWrapper(response, getDispatcher()
                .getProviderFactory()) {

            protected OutputStream getSuperOuptutStream() throws IOException {
                return super.getOutputStream();
            }

            public OutputStream getOutputStream() throws IOException {
                return new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        getSuperOuptutStream().write(b);
                    }

                    @Override
                    public void write(byte[] b) throws IOException {
                        getSuperOuptutStream().write(b);
                    }

                    @Override
                    public void write(byte[] b, int off, int len)
                            throws IOException {
                        getSuperOuptutStream().write(b, off, len);
                    }

                    @Override
                    public void flush() throws IOException {
                        getSuperOuptutStream().flush();
                    }

                    @Override
                    public void close() throws IOException {
                        getSuperOuptutStream().close();
                    }

                };
            }
        };
    }

    
}
