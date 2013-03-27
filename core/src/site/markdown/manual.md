### User's Manual
This library contains APIs that are likely to be useful in almost any non trivial Java project.

#### PerformanceLogger
The class that I use most is without a doubt [PerformanceLogger][]. It does what the name implies, and is meant to be used like this:

    //...
    PerformanceLogger plog = new PerformanceLogger();
    doWork();
    plog.logElapsedAndRestart();
    doSomeMoreWork();
    plog.logElapsed();
    //...

The implementation uses [SLF4J][]. Varying with your logging configuration ([PerformanceLogger][] logs to *at.ipsquare.commons.core.util.PerformanceLogger* with log level *DEBUG*),
the code above will result in something like this written to your shiny logs:

    SomeClass.someMethod[14->16] 333ms
    SomeClass.someMethod[16->18] 222ms

If you happen to use [LOGBack][] and you want your performance related log messages formatted similar to the example above, you might find the following logback.xml snippets useful:

    <!-- ... -->
    <property name="performancePattern" value="%msg%n"/>
    <!-- ... -->
    <appender name="PERFORMANCE_OUT" class="ch.qos.logback.core.ConsoleAppender">
      <encoder>
        <pattern>${performancePattern}</pattern>
      </encoder>
    </appender>
    <!-- ... -->
    <logger name="at.ipsquare.commons.core.util.PerformanceLogger" level="DEBUG" additivity="false">
       <appender-ref ref="PERFORMANCE_OUT"/>
     </logger>
    <!-- ... --> 

Note that you can simply turn of performance logging by setting the log level of *at.ipsquare.commons.core.util.PerformanceLogger* to anything above *DEBUG*.
Also, you can create [PerformanceLogger][]s that only generate output, if the elapsed time exceeds a certain threshold,
and it is possible to enhance the loggers output with custom messages, like so:

    PerformanceLogger plog = new PerformanceLogger(50);
    doWork();
    plog.logElapsed("Finally done");

If you are not content with the output that is produced by [PerformanceLogger][], or if you need something special, 
you can implement your own [PerformanceLogFormatter][]. The defaults used by [PerformanceLogger.PerformanceLogger()][] can be customized
by adding an XML properties file called *at/ipsquare/commons/core/util/performanceLogger.xml* to the classpath. The recognized entries (which are all optional) are:

* *defaultPerformanceLogFormatter*: The fully qualified class name of a [PerformanceLogFormatter][] implementation.
* *defaultThreshold*: The threshold in milliseconds.

Here is an example:

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">

    <properties>
        <entry key="defaultPerformanceLogFormatter">my.very.special.HandCraftedPerformanceLogFormatter</entry>
        <entry key="defaultThreshold">2</entry>
    </properties>

#### Class and Resource Loading
* [ClassLoaders][] is a global registry for class loaders that is used by all IP SQUARE commons components (this is especially true for [LocalResources][] and [Classes][]). 
* [LocalResources][] is an utility class for loading local resources from the classpath. You should use it whenever you want to load some data from the classpath, 
  like default configurations, images packaged into a JAR file, or test data needed for unit tests.
* [Classes][] is an utility class for class loading.

#### UnitOfWork
[UnitOfWork][] represents some unit of work, that is typically, but not necessarily, executed by an [UnitOfWorkExecutor][] implementation. Good examples of concrete 
[UnitOfWork][] implementations are:

* Some code containing database operations that are meant to fail atomically.
* The processing of HTTP requests in a web application.
* The processing of SOAP/REST requests in a web service.
* A piece of code that should be executed with a different logging configuration.

Note that you should normally extend [AbstractUnitOfWork][] instead of implementing [UnitOfWork][] directly.

#### Miscellaneous
[StackTrace][] contains utility methods around [Thread.getStackTrace()](http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#getStackTrace%28%29)
that are used for implementing [PerformanceLogger][], but might as well be useful in another context. [HasId][] and [StringGenerator][]
are two interfaces I employed successfully in multiple projects.

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
[Classes]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/Classes.html
[ClassLoaders]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/ClassLoaders.html
[PerformanceLogger.PerformanceLogger()]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/PerformanceLogger.html#PerformanceLogger%28%29
[PerformanceLogFormatter]: http://ipsquarecommons.sourceforge.net/ipsquare-commons-core/apidocs/at/ipsquare/commons/core/util/PerformanceLogFormatter.html
