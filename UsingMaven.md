# Introduction #

You can add the htmleasy maven repository to your projects'  POM file to make starting and maintaining your project a little easier.


### Maven repository details ###
In addition to the central maven repository, you'll need to add the following repository.
```
    <repository>
      <id>htmleasy</id>
      <url>http://htmleasy-maven.googlecode.com/svn/trunk/</url>
   </repository>
```

### Library artifacts ###
Add the core htmleasy library with:
```
    <dependency>
      <groupId>com.googlecode.htmleasy</groupId>
      <artifactId>htmleasy</artifactId>
      <version>0.7</version>
   </dependency>
```

You may choose to add extra dependencies to your project (e.g. extra Resteasy modules for JSON, DI support, etc.).  For example to setup Guice integration, add dependencies to your Maven file like:
```
   <dependency>
      <groupId>org.jboss.resteasy</groupId>
      <artifactId>resteasy-guice</artifactId>
      <version>2.2.1.GA</version>
   </dependency>
   <dependency>
      <groupId>net.sf.scannotation</groupId>
      <artifactId>scannotation</artifactId>
      <version>1.0.2</version>
   </dependency>
```

You'll find more plugin/add-on/dependeny options over in the Resteasy  [documentation on Maven](http://docs.jboss.org/resteasy/docs/2.2.1.GA/userguide/html/Maven_and_RESTEasy.html)

The Maven repository is maintained in a separate source code repository to the core Htmleasy code, and is maintained by project members and kept in sync with releases.