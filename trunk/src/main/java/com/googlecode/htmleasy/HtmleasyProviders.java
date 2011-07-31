package com.googlecode.htmleasy;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.htmleasy.provider.RedirectExceptionMapper;
import com.googlecode.htmleasy.provider.ViewExceptionMapper;
import com.googlecode.htmleasy.provider.ViewWriter;

/**
 * This class providers a static helper method to assist with registering the Htmleasy provider classes in your JAX-RS
 * Application interface without the need to use slow classpath scanning to locate the providers.
 * 
 * To use in your JAX-RS Application implementation, use code like:
 * 
 * <code>
 *   public Set<Class<?>> getClasses() {
 *      Set<Class<?>> myResources = new HashSet<Class<?>>();
 *      myResources.add(myOtherRestStuff.class);
 *      
 *      myResources.addAll(HtmleasyProviders.getClasses());
 *      
 *      return myResources;
 *   }
 * </code>
 * 
 * 
 * @author Chris Dance <chris.dance@papercut.com>
 */
public class HtmleasyProviders
{

    /** Private constructor on utility class */
    private HtmleasyProviders() {}

    /**
     * @return The list of JAX-RS providers that need to be registered with ReayEasy (via the Application class).
     */
    public static Set<Class<?>> getClasses()
    {

        Set<Class<?>> providers = new HashSet<Class<?>>(3);

        providers.add(RedirectExceptionMapper.class);
        providers.add(ViewExceptionMapper.class);
        providers.add(ViewWriter.class);

        return providers;
    }

}
