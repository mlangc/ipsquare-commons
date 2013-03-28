### User's Manual

This library is meant to contain commonly useful [Servlet][]s, [Filter][]s as well as related APIs.

#### HibernateUnitOfWorkFilter
The most useful component of the library is without a doubt [HibernateUnitOfWorkFilter][].
It executes your web requests in [UnitOfWork][] instances, that are submitted against configurable [HibernateRepository][]s,
thereby implementing the [session-per-request](http://docs.jboss.org/hibernate/orm/4.1/devguide/en-US/html/ch02.html#session-per-request) pattern.
To use it you need [ipsquare-commons-hibernate][]; Maven users should add the following dependency to their POMs:
    
    <dependency>
        <groupId>at.ipsquare</groupId>
        <artifactId>ipsquare-commons-hibernate</artifactId>
        <version>2.0.1</version>
    </dependency> 

Now simply add the filter to your web.xml like in the snippet below:

    <!-- ... -->
    <filter>
        <filter-name>HibernateUnitOfWorkFilter</filter-name>
        <filter-class>at.ipsquare.commons.servlet.HibernateUnitOfWorkFilter</filter-class>
        
        <init-param>
            <param-name>someNameForLogging</param-name>
            <param-value>at.lnet.blog.ipsquare.commons.servlet.SomeHibernateRepositoryProvider</param-value>
        </init-param>
        
        <init-param>
            <param-name>excludePathPattern</param-name>
            <param-value>^/media/.*$</param-value>
        </init-param>
        
        <init-param>
            <param-name>includePathPattern</param-name>
            <param-value>^.*\.do$</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>HibernateUnitOfWorkFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <!-- ... -->

Let’s look at the configuration in more detail: 
The first parameter (starting at line 6) is the most important. It contains an arbitrary name (everything except *excludePathPattern* and *excludePathPattern* is possible)
that is used for logging, together with a fully qualified name of a [HibernateRepositoryProvider][] that might look like the one given below:

    public class SomeHibernateRepositoryProvider implements HibernateRepositoryProvider
    {
        @Override
        public HibernateRepository get()
        {
            return InjectorHome.get().getInstance(HibernateRepository.class);
        }
    }

*InjectorHome.get()* is meant to return a reference your [Injector][] (I know that one should avoid directly referencing the injector wherever possible, 
but here we simply have to. Most likely this could be done slightly more elegant using a [ServletModule][], but [ServletModule][] has problems of its own 
and I don’t want the library to depend on it). Of course you are free to implement your [HibernateRepositoryProvider][] completely different (for example by using another DI framework). 
If your application happens to use more than one database, you can simply add init parameters for additional providers.

The remaining parameters, *excludePathPattern* and *includePathPattern* 
in lines 11 and 16 are optional and are actually processed by a [PathPatternRequestMatcher][], which brings us directly to the next topic.

#### Request matchers:
Request matchers are implementations of [RequestMatcher][]. Currently, there are three implementations coming with [ipsquare-commons-servlet][], 
[two of them](http://ipsquarecommons.sourceforge.net/ipsquare-commons-servlet/apidocs/at/ipsquare/commons/servlet/TrivialRequestMatcher.html) being trivial. 
[PathPatternRequestMatcher][], that was already mentioned before, matches relative paths (without the context path) 
against configurable regular expressions. To ease regular expression debugging when configuring the matcher, 
you might want to switch the log level of [PathPatternRequestMatcher][] to *DEBUG* temporarily.

#### RequestEncodingFilter:
[RequestEncodingFilter][] is a simple filter that takes care of setting the characer encoding in [ServletRequest][]s. It's quite similar to 
[this filter](http://static.springsource.org/spring/docs/3.0.x/api/org/springframework/web/filter/CharacterEncodingFilter.html) that comes with [Spring][].
Unless you have special needs, you might want to use it like so:

    <!-- ... -->
    <filter>
        <filter-name>RequestEncodingFilter</filter-name>
        <filter-class>at.ipsquare.commons.servlet.RequestEncodingFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>RequestEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <!-- ... -->

This will set the requests character encoding to *UTF-8* unless it has already been set. If this is not exactly what you want, you can use
the following *init parameters* for tuning:

+ *encoding*: The character encoding to set (defaults to *UTF-8*).
+ *forceEncoding*: If this parameter is true, the filter will overwrite any previously set encodings (defaults to *false*).
+ *includePathPattern* and *excludePathPattern*: These parameters are are processed by a [PathPatternRequestMatcher][] (see above); the encoding is only set if a match occurs.

**Just make sure that the filter is called before you start processing the request in any way!** Otherwise it will have no effect, as stated by
Javadocs of [HttpServletRequest.setCharacterEncoding(...)](http://docs.oracle.com/javaee/6/api/javax/servlet/ServletRequest.html#getCharacterEncoding%28%29).

#### PerformanceLogFilter
[PerformanceLogFilter][] is a servlet filter that logs the time spent processing the requests that go through it. With my logging configuration 
a typical log entry produced by [PerformanceLogFilter][] looks like this:

    15:46:28 PERFORMANCE 15ms <<GET /html/index.html>>

If you don't need anything special, adding the following snippet to your *web.xml* should do the trick:

    <!-- ... -->
    <!-- Dont forget to configure your logging framework accordingly if you want to see any output!-->
    <filter>
        <filter-name>PerformanceLogFilter</filter-name>
        <filter-class>at.ipsquare.commons.servlet.PerformanceLogFilter</filter-class>
    </filter>

    <filter-mapping>
        <filter-name>PerformanceLogFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <!-- ... -->

[PerformanceLogFilter][] uses a [PerformanceLogger][] from [ipsquare-commons-core][]. To see any output you have to set the log level
of [PerformanceLogger][] to *DEBUG* or below. Further tweaking is possible using the following *init parameters*, that are all optional:

+ *excludePathPattern* and *includePathPattern*: See [PathPatternRequestMatcher][].
+ *threshold*: The threshold to use for performance logging (see [PerformanceLogger.PerformanceLogger(threshold)][]).
+ *prefix*: A prefix that is used to mark the log messages (useful if you want to filter them later).
+ *performanceLogFormatter*: The fully qualified class name of a [PerformanceLogFormatter][].
+ *performanceLogFilterMessageFormatter*: The fully qualified class name of a [PerformanceLogFilterMessageFormatter][].


[PerformanceLogger]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/PerformanceLogger.html 
[SLF4J]: http://www.slf4j.org/
[LOGBack]: http://logback.qos.ch/
[Javadocs]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/PerformanceLogger.html
[UnitOfWork]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/interfaces/UnitOfWork.html
[UnitOfWorkExecutor]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/interfaces/UnitOfWorkExecutor.html
[AbstractUnitOfWork]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/interfaces/AbstractUnitOfWork.html
[StackTrace]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/StackTrace.html
[HasId]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/interfaces/HasId.html
[StringGenerator]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/interfaces/StringGenerator.html
[LocalResources]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/LocalResources.html
[DefaultHibernateRepository]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/apidocs/at/ipsquare/commons/hibernate/DefaultHibernateRepository.html
[ipsquare-commons-hibernate]: http://search.maven.org/#browse|1336285788
[google-guice]: http://code.google.com/p/google-guice/
[HibernateRepository]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/apidocs/at/ipsquare/commons/hibernate/HibernateRepository.html
[HibernateRepository.executeUnitOfWork(…)]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/apidocs/at/ipsquare/commons/hibernate/HibernateRepository.html#executeUnitOfWork%28at.ipsquare.interfaces.UnitOfWork%29
[HibernateRepository.currentSession()]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/apidocs/at/ipsquare/commons/hibernate/HibernateRepository.html#currentSession%28%29
[UnitOfWork.execute()]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/interfaces/UnitOfWork.html#execute%28%29
[ExecutionError]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/interfaces/ExecutionError.html
[IllegalStateException]: http://docs.oracle.com/javase/7/docs/api/java/lang/IllegalStateException.html
[AbstractHibernateConfiguration]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/apidocs/at/ipsquare/commons/hibernate/AbstractHibernateConfiguration.html
[DefaultHibernateRepository]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/apidocs/at/ipsquare/commons/hibernate/DefaultHibernateRepository.html
[ipsquare-commons-servlet]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-servlet/index.html
[HibernateUnitOfWorkFilter]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-servlet/apidocs/at/ipsquare/commons/servlet/HibernateUnitOfWorkFilter.html
[HibernateConfiguration]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/apidocs/at/ipsquare/commons/hibernate/HibernateConfiguration.html
[SessionFactory]: http://docs.jboss.org/hibernate/orm/4.1/javadocs/org/hibernate/SessionFactory.html
[DefaultHibernateRepository.DefaultHibernateRepository(…)]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/apidocs/at/ipsquare/commons/hibernate/DefaultHibernateRepository.html#DefaultHibernateRepository%28at.ipsquare.hibernate.HibernateConfiguration%29
[NoClassDefFoundError]: http://docs.oracle.com/javase/7/docs/api/java/lang/NoClassDefFoundError.html
[Servlet]: http://docs.oracle.com/javaee/6/api/javax/servlet/Servlet.html
[Filter]: http://docs.oracle.com/javaee/6/api/javax/servlet/Filter.html
[HibernateRepositoryProvider]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/apidocs/at/ipsquare/commons/hibernate/HibernateRepositoryProvider.html
[Injector]: http://google-guice.googlecode.com/git/javadoc/com/google/inject/Injector.html
[ServletModule]: http://code.google.com/p/google-guice/wiki/ServletModule
[PathPatternRequestMatcher]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-servlet/apidocs/at/ipsquare/commons/servlet/PathPatternRequestMatcher.html
[RequestMatcher]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-servlet/apidocs/at/ipsquare/commons/servlet/RequestMatcher.html
[RequestEncodingFilter]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-servlet/apidocs/at/ipsquare/commons/servlet/RequestEncodingFilter.html
[PerformanceLogFilter]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-servlet/apidocs/at/ipsquare/commons/servlet/PerformanceLogFilter.html
[ServletRequest]: http://docs.oracle.com/javaee/6/api/javax/servlet/ServletRequest.html
[Spring]: http://www.springsource.org/
[PerformanceLogger.PerformanceLogger()]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/PerformanceLogger.html#PerformanceLogger%28%29
[PerformanceLogFormatter]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/PerformanceLogFormatter.html
[ipsquare-commons-core]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/index.html
[PerformanceLogger.PerformanceLogger(threshold)]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/PerformanceLogger.html#PerformanceLogger%28long%29
[PerformanceLogFilterMessageFormatter]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-servlet/apidocs/at/ipsquare/commons/servlet/PerformanceLogFilterMessageFormatter.html
[ipsquare-commons-hibernate]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-hibernate/index.html
