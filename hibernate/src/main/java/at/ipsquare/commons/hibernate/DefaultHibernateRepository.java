/**
 * Copyright (C) 2013 Matthias Langer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ipsquare.commons.hibernate;

import java.lang.ref.WeakReference;
import java.sql.Driver;
import java.util.Map;

import net.jcip.annotations.ThreadSafe;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ipsquare.commons.core.interfaces.ExecutionError;
import at.ipsquare.commons.core.interfaces.UnitOfWork;
import at.ipsquare.commons.core.util.Classes;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Default {@link HibernateRepository} implementation.
 * 
 * @since 2.0.0
 * @author Matthias Langer
 */
@Singleton
@ThreadSafe
public class DefaultHibernateRepository implements HibernateRepository
{
    private static final Logger log = LoggerFactory.getLogger(DefaultHibernateRepository.class);
    
    private static final 
        Map<Class<? extends DefaultHibernateRepository>, WeakReference<? extends DefaultHibernateRepository>> 
            instanceMap = Maps.newHashMapWithExpectedSize(2);
             
    private final SessionFactory sessionFactory;
    private final ThreadLocal<Session> currentSession = new ThreadLocal<Session>();
    private final ThreadLocal<UnitOfWork<?>> currentUnitOfWork = new ThreadLocal<UnitOfWork<?>>();
    
    
    @Inject
    public DefaultHibernateRepository(HibernateConfiguration hibernateCfg)
    {
        synchronized(instanceMap)
        {
            WeakReference<? extends DefaultHibernateRepository> ref = instanceMap.get(getClass());
            if(ref != null)
            {
                DefaultHibernateRepository other = ref.get();
                if(other != null && !other.isClosed())
                {
                    throw new IllegalStateException("Attempting to create an instance of " + getClass().getSimpleName() + " while " +
                    		"another instance that has not yet been closed is still weakly reachable. " +
                    		"You are not meant to have more than one open repository for the same underlying DB resource. " +
                    		"Plese read the documentation and fix your code.");
                            
                }
            }

            instanceMap.put(getClass(), new WeakReference<DefaultHibernateRepository>(this));
        }

        sessionFactory = buildSessionFactory(hibernateCfg);
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
    
    @Override
    public void close()
    {
        sessionFactory.close();
    }
    
    @Override
    public boolean isClosed()
    {
        return sessionFactory.isClosed();
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
    
    private static SessionFactory buildSessionFactory(HibernateConfiguration hibernateCfg)
    {
        Configuration nativeCfg = toNativeHibernateConfiguration(hibernateCfg);
        ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
        builder.applySettings(nativeCfg.getProperties());
        return nativeCfg.buildSessionFactory(builder.buildServiceRegistry());
    }
    
    private static Configuration toNativeHibernateConfiguration(HibernateConfiguration hibernateCfg)
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
            Classes.forName(clazz.getName());
        }
        catch(ClassNotFoundException e)
        {
            throw new RuntimeException("Could not load database driver.", e);
        }
    }
}
