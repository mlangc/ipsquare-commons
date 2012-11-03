package at.ipsquare.hibernate;

import java.sql.Driver;

import com.google.inject.Singleton;

@Singleton
public class UnitTestHibernateConfiguration extends AbstractHibernateConfiguration
{
    public String getDbUser()
    {
        return "sa";
    }
    
    public String getDbPass()
    {
        return "";
    }
    
    public Class<? extends Driver> getDbDriverClass()
    {
        return org.h2.Driver.class;
    }
    
    public String getDbConnectionUrl()
    {
        return "jdbc:h2:mem:test";
    }
    
    @Override
    public HibernateHbm2dllAuto getHbm2dllAuto()
    {
        return HibernateHbm2dllAuto.CREATE_DROP;
    }
    
    public Class<?>[] getDomainClasses()
    {
        return new Class<?>[] {
          UnitTestEntityParent.class, UnitTestEntityChild.class      
        };
    }
}
