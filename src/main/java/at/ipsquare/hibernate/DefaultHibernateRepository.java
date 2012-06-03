package at.ipsquare.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Default {@link HibernateRepository} implementation.
 * 
 * @author Matthias Langer
 * 
 * TODO: Logging!
 */
@Singleton
public class DefaultHibernateRepository implements HibernateRepository
{
    private final SessionFactory sessionFactory;
    
    @Inject
    public DefaultHibernateRepository(HibernateConfiguration hibernateCfg)
    {
        sessionFactory = sessionFactory(hibernateCfg);
    }
    
    public <T> T executeUnitOfWork(UnitOfWork<T> work)
    {
        Session session = session();
        Transaction tx = session.beginTransaction();
        try
        {
            T result = null;
            try
            {
                result = work.exectute(session);
            }
            catch(Exception e)
            {
                tx.rollback();
                throw new ExecutionError(e);
            }
            
            tx.commit();
            return result;
        }
        finally
        {
            session.close();
        }
    }
    
    private Session session()
    {
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
        
        return cfg;
    }
}
