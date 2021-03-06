# Htmleasy Installation #

**On this page:**

  1. [Setup from scratch](#From_scratch.md)
  1. [Setup in an existing Resteasy project](#For_existing_projects_using_Resteasy.md)
  1. [Setup in a dependency injection environment](#For_existing_projects_using_Resteasy_with_DI.md)
  1. [Setup using Maven/Ivy](#Using_Maven.md)
  1. [Demo/Example Project](#Demo/Example_Project.md)


This page explains how to install and configure Htmleasy.  To quickly get a feel for Htmleasy try downloading the preassembled [Htmleasy Playground](HtmleasyPlayground.md) project (a Google App Engine project).

## From scratch ##

**Step 1:** Set up a standard WAR project hosted inside you're servlet container of choice. (e.g. [Jetty](http://jetty.codehaus.org/jetty/), [Tomcat](http://tomcat.apache.org/), [Google App Engine](http://code.google.com/appengine/docs/java/overview.html), J2EE Server)

**Step 2:** Add the appropriate [Resteasy](http://www.jboss.org/resteasy) JARs to your classpath. (e.g. place in your WEB-INF/lib directory)

**Step 2:** Add the [htmleasy.jar](http://code.google.com/p/htmleasy/downloads/list) to your classpath.

**Step 3:** Edit your `WEB-INF/web.xml` file as follows:

```
<?xml version="1.0" encoding="utf-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns="http://java.sun.com/xml/ns/javaee"
xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" version="2.5">

    <filter>
        <filter-name>Htmleasy</filter-name>
        <filter-class>
            com.googlecode.htmleasy.HtmleasyFilterDispatcher
        </filter-class>
        <init-param>
            <param-name>javax.ws.rs.Application</param-name>
            <param-value>com.myorg.myproject.MyApp</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>Htmleasy</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <jsp-property-group>
        <description>Common config for all the JSP</description>
        <url-pattern>*.jsp</url-pattern>
        <el-ignored>false</el-ignored>
    </jsp-property-group>
	
</web-app>
```

_Note:_ Change `com.myorg.myproject.MyApp` as appropriate. See next step.

**Step 4:** Create an implementation of `javax.ws.rs.core.Application` as follows:

```
package com.myorg.myproject;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;
import com.googlecode.htmleasy.HtmleasyProviders;

public class MyApp extends Application {

	 public Set<Class<?>> getClasses() {
	      Set<Class<?>> myServices = new HashSet<Class<?>>();
	      
	      // Add my own JAX-RS annotated classes
	      myServices.add(TheTime.class);
	      
	      // Add Htmleasy Providers
	      myServices.addAll(HtmleasyProviders.getClasses());
	      
	      return myServices;
	 }
}
```

See the [Resteasy documentation](http://docs.jboss.org/resteasy/docs/2.2.1.GA/userguide/html_single/index.html#javax.ws.rs.core.Application) for more information.


## For existing projects using Resteasy ##

**Step 1:** Add the `htmleasy.jar` to your classpath

**Step 2:** Change your **web.xml** file as follows:

  * If you have configured Resteasy as a `filter`, change `org.jboss.resteasy.plugins.server.servlet.FilterDispatcher` to `com.googlecode.htmleasy.HtmleasyFilterDispatcher`

  * If you have configured Resteasy as a **servlet**, change `org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher` to     `com.googlecode.htmleasy.HtmleasyServletDispatcher`

Please see ResteasyFilter if you'd like to know more about why this step is required.

**Step 3:** Ensure the Htmleasy `@Providers` are registered.

If you're using classpath scanning then no action should be required.  Resteasy will detect the Htmleasy providers.

Classpath scanning can cause slow startup times on larger projects. If you're registering your classes via `javax.ws.rs.Application` ensure the Htmleasy providers are added to your `getClasses()` set. There is a convenience method `HtmleasyProviders.getClasses()` to help here. For example:

```
public class MyApp extends Application {

	 public Set<Class<?>> getClasses() {
	      Set<Class<?>> myServices = new HashSet<Class<?>>();
	      
	      // Add my own JAX-RS annotated classes
	      myServices.add(MyRestStuff.class);
	      
	      // Add Htmleasy Providers
	      myServices.addAll(HtmleasyProviders.getClasses());
	      
	      return myServices;
	 }
}
```

If your JAX-RS annotated classes are registered via a Dependency Injection environment such as Guice, Spring, or CDI, see below.

## For existing projects using Resteasy with DI ##

In addition to your JAX-RS annotated classes, make sure the following Htmleasy classes are under management:

```
    com.googlecode.htmleasy.provider.ViewWriter
    com.googlecode.htmleasy.provider.RedirectExceptionMapper
    com.googlecode.htmleasy.provider.ViewExceptionMapper
```

For example, if you're using [Resteasy's Guice 2.0 Integration](http://docs.jboss.org/resteasy/docs/2.2.1.GA/userguide/html/Guice1.html) your module may look like this:

```
import com.google.inject.Module;
import com.google.inject.Binder;

public class MyModule implements Module
{
    public void configure(final Binder binder)
    {
       // Ensure Htmleasy Provider classes are found
       for (Class<?> c : HtmleasyProviders.getClasses()) {
           binder.bind(c);
       }
      
       // My classes here
       binder.bind(MyAnnotatedClass.class);
    }
}
```

## Using Maven ##

A Maven repository is maintained as part of this project.  New projects, or projects using Maven (or related services such as Ivy/Gradle) should consider using this.  More information on the [using Maven with Htmleasy page](UsingMaven.md)


## Demo/Example Project ##

To quickly get a feel for Htmleasy try downloading the preassembled **[Htmleasy Playground](http://code.google.com/p/htmleasy/downloads/list)** project (a Google App Engine Eclipse project).