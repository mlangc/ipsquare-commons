package at.ipsquare.hibernate;

import java.sql.Driver;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Default {@link HibernateRepository} implementation.
 * 
 * @since 1.0.0
 * @author Matthias Langer
 */
@Singleton
public class DefaultHibernateRepository implements HibernateRepository
{
    private static final Logger log = LoggerFactory.getLogger(DefaultHibernateRepository.class);
    private final SessionFactory sessionFactory;
    private final ThreadLocal<Session> currentSession = new ThreadLocal<Session>();
    private final ThreadLocal<UnitOfWork<?>> currentUnitOfWork = new ThreadLocal<UnitOfWork<?>>();
    
    @Inject
    public DefaultHibernateRepository(HibernateConfiguration hibernateCfg)
    {
        sessionFactory = sessionFactory(hibernateCfg);
    }
    
    public <T> T executeUnitOfWork(UnitOfWork<T> work)
    {
        if(work == null)
            throw new NullPointerException();
        
        if(currentUnitOfWork.get() != null)
        {
            log.info("Executing " + work + " within already running " + currentUnitOfWork.get() + ".");
            try
            {
                return work.execute();
            }
            catch(Exception e)
            {
                log.warn(exceptionLogMessage(work), e);
                throw new ExecutionError(e);
            }
        }
        
        log.info("Executing " + work + ".");
        Session session = session();
        currentSession.set(session);
        currentUnitOfWork.set(work);
        
        session.beginTransaction();
        try
        {
            T result = null;
            try
            {
                result = work.execute();
            }
            catch(Exception e)
            {
                log.warn(exceptionLogMessage(work), e);
                
                Transaction tx = getActiveTransaction(session);
                if(tx != null)
                    tx.rollback();
                throw new ExecutionError(e);
            }
            
            Transaction tx = getActiveTransaction(session);
            if(tx != null)
                tx.commit();
            return result;
        }
        finally
        {
            try
            {
                log.info("Closing " + work + ".");
                
                /*
                 * TODO (mla):
                 *  Eventually issue a warning if the session is already closed.
                 */
                session.close();
            }
            finally
            {
                currentSession.set(null);
                currentUnitOfWork.set(null);
            }
        }
    }
    
    private static String exceptionLogMessage(UnitOfWork<?> work)
    {
        return "Executing " + work + " resulted in an exception.";
    }
    
    private static Transaction getActiveTransaction(Session session)
    {
        Transaction tx = session.getTransaction();
        if(tx != null && tx.isActive())
            return tx;
        return null;
    }
    
    public Session currentSession()
    {
        if(currentUnitOfWork.get() == null)
            throw new IllegalStateException("Attempting to access the current session without a UnitOfWork.");
        
        if(currentSession.get() == null)
            throw new AssertionError("Should be impossible.");
        
        return currentSession.get();
    }
    
    private Session session()
    {
        if(currentSession.get() != null)
            return currentSession.get();
        return sessionFactory.openSession();
    }
    
    private static SessionFactory sessionFactory(HibernateConfiguration hibernateCfg)
    {
        return hibernateConfiguration(hibernateCfg).buildSessionFactory();
    }
    
    private static Configuration hibernateConfiguration(HibernateConfiguration hibernateCfg)
    {
        Configuration cfg = new Configuration();
        for(Class<?> domainClass : hibernateCfg.getDomainClasses())
            cfg.addAnnotatedClass(domainClass);
        
        cfg.setProperty("hibernate.connection.username", hibernateCfg.getDbUser());
        cfg.setProperty("hibernate.connection.password", hibernateCfg.getDbPass());
        cfg.setProperty("hibernate.connection.url", hibernateCfg.getDbConnectionUrl());
        cfg.setProperty("hibernate.connection.driver_class", hibernateCfg.getDbDriverClass().getCanonicalName());
        cfg.setProperty("hibernate.hbm2ddl.auto", hibernateCfg.getHbm2dllAuto().toString());
        
        if(hibernateCfg.getProperties() != null)
        {
            for(Map.Entry<String, String> entry : hibernateCfg.getProperties().entrySet())
                cfg.setProperty(entry.getKey(), entry.getValue());
        }
        
        explicitlyInitializeDriverClass(hibernateCfg.getDbDriverClass());
        return cfg;
    }
    
    /**
     * This method makes sure that the database driver is properly initialized.
     * 
     * @see HibernateConfiguration#getDbDriverClass()
     */
    private static void explicitlyInitializeDriverClass(Class<? extends Driver> clazz)
    {
        try
        {
            Class.forName(clazz.getCanonicalName());
        }
        catch(ClassNotFoundException e)
        {
            throw new RuntimeException("Could not load database driver.", e);
        }
    }
}
