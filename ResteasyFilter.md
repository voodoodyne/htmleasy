To use Htmleasy, your Resteasy configuration needs to be setup using a special filter or servlet.  Due to technical reasons it's not possible to use the default Resteasy filter/servlet.  To configure your server simply follow the [installation instructions](Installation.md).

The Htmleasy dispatch filter/servlet works exactly the same way as the default Reaseasy filter/servlet.  You may continue to use all standard Resteasy [configuration switches](http://docs.jboss.org/resteasy/docs/2.2.1.GA/userguide/html/Installation_Configuration.html#d0e72).

## Technical Explanation: ##
Unfortunately Resteasy opens the servlet output writter a little early, before Htmleasy has had an opportunity to forward the request. This causes problems under some servlet environments.  The Htmleasy filter delays the stream's open/create until the first write.


_(Explanation current as of Resteasy 2.2.1)_