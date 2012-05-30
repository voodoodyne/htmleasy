package com.googlecode.htmleasy;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.plugins.server.servlet.FilterDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.spi.HttpResponse;

/**
 * Special HTTP filter to support HtmlEasy. To use, add something like this to your web.xml:
 * 
 *   <filter>
 *       <filter-name>HtmleasyFilter</filter-name>
 *       <filter-class>com.googlecode.htmleasy.HtmleasyFilterDispatcher</filter-class>
 *   </filter>
 *           
 *   <filter-mapping>
 *       <filter-name>HtmleasyFilter</filter-name>
 *       <url-pattern>/*</url-pattern>
 *   </filter-mapping>
 * 
 * Because Htmleasy forwards through to another view (e.g. a JSP page) the
 * output stream must remain closed. This filter defers opening/creating the stream until a write is performed.
 * 
 * Implementation based on the HTMLServletDispatcher.java file found in the RestEasy SCM located at:
 * ~trunk/jaxrs/providers/resteasy-html/src/main/java/org/jboss/resteasy/plugins/providers/html/HtmlServletDispatcher.java
 * 
 * @author Chris Dance <chris.dance@papercut.com>
 */
public class HtmleasyFilterDispatcher extends FilterDispatcher {

   @Override
   public HttpResponse createResteasyHttpResponse(HttpServletResponse response) {
       return new HttpServletResponseWrapper(response, getDispatcher().getProviderFactory()) {

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
