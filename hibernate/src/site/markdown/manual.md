### User Manual

This library contains a [UnitOfWorkExecutor][] implementation, namely [DefaultHibernateRepository][], that should greatly help you with Hibernate session and transaction management
in a non managed environment. [ipsquare-commons-hibernate][] has been designed with [google-guice][] in mind, and I will therefore demonstrate its usage with the dependency
injection framework just mentioned (but see the section at the end of this article if you are interested in using the library without Guice).
Most applications use no more than a single database, so this is the use case we are going to look into first:

#### Usage for applications that use exactly one database:
Assume that your application serves some kind of requests, so strongly simplified, we want to implement something like this:

    public interface SomeApplication
    {
        int serveRequest();
    }

Supposing that the needed Guice bindings are in place, a possible implementation using [HibernateRepository][] can be seen below:


    @Singleton
    public class SomeApplicationImpl implements SomeApplication
    {
        private final SomeInterface someInterface;
        private final HibernateRepository repo;
        
        @Inject
        SomeApplicationImpl(SomeInterface someInterface, HibernateRepository repo)
        {
            this.someInterface = someInterface;
            this.repo = repo;
        }

        public int serveRequest()
        {
            return repo.executeUnitOfWork(new AbstractUnitOfWork<Integer>()
            {
                @Override
                public Integer execute() throws Exception
                {
                    return someInterface.doStuff();
                }
            });
        }
    }

The implementation uses a [HibernateRepository][] to execute a [UnitOfWork][], that then just calls an interface, that is implemented as follows:

    @Singleton
    public class SomeImplementation implements SomeInterface
    {
        private final HibernateRepository repo;

        @Inject
        SomeImplementation(HibernateRepository repo)
        {
            this.repo = repo;
        }
        
        @Override
        public int doStuff()
        {
            Session session = repo.currentSession();
            // ...
            // read, write, delete, update as needed.
            // no need to close, commit or rollback.
            // ...
            return 42;
        }
    }

Before elaborating on the Guice configuration that makes the snippets above actually work, a few explanations are in order: [HibernateRepository.executeUnitOfWork(…)][] opens a 
Hibernate session as well as a database transaction and calls [UnitOfWork.execute()][]. The associated Hibernate session can 
be accessed as needed by calling [HibernateRepository.currentSession()][]. 
If everything goes well, the transaction is committed, and the return value of [UnitOfWork.execute()][] is forwarded. 
If [UnitOfWork.execute()][] throws, the transaction is rolled back, and the problematic exception is wrapped in an [ExecutionError][].
The associated Hibernate session is closed in either case. Calling [HibernateRepository.currentSession()][]
from outside an associated [UnitOfWork][] is a severe error and results in an [IllegalStateException][]. If you need more fine grained transaction management,
you can always call transaction related methods on the session returned by [HibernateRepository.currentSession()][];
or you can simply split your operation into multiple units of work. There is one more thing that should be explicitly mentioned before moving on:
It is perfectly legitimate to call[ HibernateRepository.executeUnitOfWork(…)][] while another [UnitOfWork][]
is already being executed by the same repository. In this case the session and the transaction are not modified at all,
as they are already owned by the outer [UnitOfWork][]. This makes it possible to implement APIs that work regardless of 
them being called inside or outside a [UnitOfWork][].

So, this is everything? No, of course not: At first, Hibernate still wants to be configured: That is done by subclassing [AbstractHibernateConfiguration][]:

    @Singleton
    public class SomeHibernateConfiguration extends AbstractHibernateConfiguration
    {
        @Override
        public String getDbConnectionUrl()
        {
            return "jdbc:h2:mem:test";
        }
        
        @Override
        public Class<? extends Driver> getDbDriverClass()
        {
            return org.h2.Driver.class;
        }
        
        @Override
        public String getDbPass()
        {
            return "";
        }
        
        @Override
        public String getDbUser()
        {
            return "sa";
        }
        
        @Override
        public Class<?>[] getDomainClasses()
        {
            return new Class<?>[] {
                // your entity classes here.
            };
        }
        
        @Override
        public HibernateHbm2dllAuto getHbm2dllAuto()
        {
            return HibernateHbm2dllAuto.UPDATE;
        }
    }

Last but not least, we need a Guice module wiring everything together:

    public class SomeGuiceModule extends AbstractModule
    {
        @Override
        protected void configure()
        {
            bind(SomeApplication.class).to(SomeApplicationImpl.class);
            bind(SomeInterface.class).to(SomeImplementation.class);
            bind(HibernateRepository.class).to(DefaultHibernateRepository.class);
            bind(HibernateConfiguration.class).to(SomeHibernateConfiguration.class);
        }
    }

Note the binding in line 8: [DefaultHibernateRepository][] is a [HibernateRepository][] implementation that comes with [ipsquare-commons-hibernate][]. 
Last but not least: If your application lives in a servlet container, consider using [HibernateUnitOfWorkFilter][] from [ipsquare-commons-servlet][].

#### Usage for applications with multiple databases:
Now assume that we are once again implementing

    public interface SomeApplication
    {
        int serveRequest();
    }

but this time we need two different databases to do so. So at first, we create two interfaces to access these repositories:

```
public interface HibernateRepositoryA extends HibernateRepository
{
    
}
```

```
public interface HibernateRepositoryB extends HibernateRepository
{
    
}
```

Using these, we can already start implementing our application like so:#

    @Singleton
    public class SomeApplicationImpl implements SomeApplication
    {
        private final SomeInterface someInterface;
        private final HibernateRepositoryA repoA;
        private final HibernateRepositoryB repoB;
        
        @Inject
        SomeApplicationImpl(SomeInterface someInterface, HibernateRepositoryA repoA, HibernateRepositoryB repoB)
        {
            this.someInterface = someInterface;
            this.repoA = repoA;
            this.repoB = repoB;
        }

        @Override
        public int serveRequest()
        {
            return repoA.executeUnitOfWork(new AbstractUnitOfWork<Integer>()
            {
                @Override
                public Integer execute() throws Exception
                {
                    return repoB.executeUnitOfWork(new AbstractUnitOfWork<Integer>()
                    {
                        @Override
                        public Integer execute() throws Exception
                        {
                            return someInterface.doStuff();
                        }
                    });
                }
            });
        }
    }

The code in *serveRequest()* actually looks far more complicated than it actually is: 
All we are doing is executing *SomeInterface.doStuff()* in a nested [UnitOfWork][] of *repoA* and *repoB*, 
so that the implementation of *SomeInterface* can use both repositories conveniently, like below:

    @Singleton
    public class SomeImplementation implements SomeInterface
    {
        private final HibernateRepositoryA repoA;
        private final HibernateRepositoryB repoB;
        
        @Inject
        SomeImplementation(HibernateRepositoryA repoA, HibernateRepositoryB repoB)
        {
            this.repoA = repoA;
            this.repoB = repoB;
        }

        @Override
        public int doStuff()
        {
            Session sessionA = repoA.currentSession();
            Session sessionB = repoB.currentSession();
            // ...
            // read, write, delete, update as needed.
            // no need to close, commit or rollback.
            // ...
            return 42;
        }
    }

Of course, we still need implementations for *HibernateRepositoryA* and *HibernateRepositoryB*.
These repositories will use different [HibernateConfiguration][]s, so we create interfaces for them:

```
public interface HibernateConfigurationA extends HibernateConfiguration
{

}
```

```
public interface HibernateConfigurationB extends HibernateConfiguration
{

}
```

Using these, implementing *HibernateRepositoryA* and *HibernateRepositoryB* is a triviality:


```
@Singleton
public class DefaultHibernateRepositryA extends DefaultHibernateRepository implements HibernateRepositoryA
{
    @Inject
    DefaultHibernateRepositryA(HibernateConfigurationA hibernateCfg)
    {
        super(hibernateCfg);
    }
}
```
 
```
@Singleton
public class DefaultHibernateRepositryB extends DefaultHibernateRepository implements HibernateRepositoryB
{
    @Inject
    DefaultHibernateRepositryB(HibernateConfigurationB hibernateCfg)
    {
        super(hibernateCfg);
    }
}
```

Note the **@Singleton** annotation on these classes: **It is absolutely vital that there is at most one open [DefaultHibernateRepository][] per database resource.**
As [DefaultHibernateRepository][] wraps a [SessionFactory][], anything else would almost certainly lead to tears. To save you from nasty surprises, the constructor 
[DefaultHibernateRepository.DefaultHibernateRepository(…)][] terminates with an exception if you attempt to create another instance of the 
same class before closing the old one. The reason it was not necessary to mention this in the simple case where only one database is used, 
is that [DefaultHibernateRepository][] already is annotated with *@Singleton*.

What remains to be done is providing implementations for *HibernateConfigurationA* and *HibernateConfigurationB*

```
@Singleton
public class SomeHibernateConfigurationA extends AbstractHibernateConfiguration implements HibernateConfigurationA
{
    // ...
    // ...
    // ...
}
```

```
@Singleton
public class SomeHibernateConfigurationB extends AbstractHibernateConfiguration implements HibernateConfigurationB
{
    // ...
    // ...
    // ...
}
```

and wiring everything together in a Guice module:

    public class SomeGuiceModule extends AbstractModule
    {
        @Override
        protected void configure()
        {
            bind(SomeApplication.class).to(SomeApplicationImpl.class);
            bind(SomeInterface.class).to(SomeImplementation.class);
            bind(HibernateRepositoryA.class).to(DefaultHibernateRepositryA.class);
            bind(HibernateRepositoryB.class).to(DefaultHibernateRepositryB.class);
            bind(HibernateConfigurationA.class).to(SomeHibernateConfigurationA.class);
            bind(HibernateConfigurationB.class).to(SomeHibernateConfigurationB.class);
        }
    }

#### If you don’t want to use Guice:
There should be nothing that stops you from using [ipsquare-commons-hibernate][] with another dependency injection mechanism, 
or without dependency injection at all. The library has a compile time dependency on Guice only for a few annotations, 
so you [should](http://stackoverflow.com/questions/3567413/why-doesnt-a-missing-annotation-cause-a-classnotfoundexception-at-runtime) not get any [NoClassDefFoundError][]s if you remove Guice from the classpath entirely. There is just one thing you have be careful about (as already mentioned in more detail above): 
**[DefaultHibernateRepository][] and subclasses are meant to be singletons.**

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
