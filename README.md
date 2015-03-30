# Htmleasy #

**_Htmleasy is a simple, elegant HTML based MVC micro-framework that builds on Resteasy (JAX-RS)._**

It's now 2012! Back in 2005 the web was all about server-side state and server templating. Today's web is rapidly shifted towards a client-side focus with technologies like AJAX, JSON, `PushState`, REST and clean URLs.  There is a need for lighter-weight server-side web frameworks that facilitate this new type of development - JSON, REST and clean HTML all working as one.  Htmleasy is such a framework.  It builds on the elegant annotation based JAX-RS API to provide a clearly separated model-view-controller environment without the "Web 1.0" baggage.

## Benefits: ##

  * Simplicity! Easy to learn. Leverage your existing JAX-RS and REST knowledge.
  * Clear separation between paths, controllers, views and models.
  * Ability to easily unit-test controllers.
  * Fast startup time. (Ideal for Google App Engine, or your own standard container)
  * Polymorphic views - change views depending on the controller model return type.
  * Resolve paths from controller methods (type-safe and refactor-safe path references).
  * Your choice of view technology.
  * Encapsulate your HTML views and supporting JSON/REST interfaces within the same controller classes.
  * No XML configuration - 100% annotation based.
  * Use all that you find in [RESTEasy](http://www.jboss.org/resteasy) - e.g. JSON, XML, choice of dependency injection frameworks, etc.

Htmleasy is best described as a simple shim or extension to JAX-RS/Resteasy, and brings JAX-RS annotations to HTML MVC.  It provides simple tools for rendering data objects as HTML views and managing basic pageflow.  The project also documents a number of common patterns.

### As easy as... ###


```
@Path("/")
public class Welcome {

	@GET @Path("/welcome/{name}")
	public View sayHi(@PathParm("name") String name)
	{
		return new View("/welcome.jsp", name);
	}
}
```

## Explore ##
See **[Using Htmleasy](UsingHtmleasy.md)** for **[more >>>](UsingHtmleasy.md)**

 _**Htmleasy** - elegance through simplicity... maybe it's all about what it doesn't do!_