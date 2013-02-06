### User's Manual

This library is meant to contain commonly useful [Servlet][]s, [Filter][]s as well as related APIs.

#### HibernateUnitOfWorkFilter
The most useful component of the library is without a doubt [HibernateUnitOfWorkFilter][].
It executes your web requests in [UnitOfWork][] instances, that are submitted against configurable [HibernateRepository][]s,
thereby implementing the [session-per-request](http://docs.jboss.org/hibernate/orm/4.1/devguide/en-US/html/ch02.html#session-per-request) pattern.
Simply add it to your web.xml like in the snippet below:

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
