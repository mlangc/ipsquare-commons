package at.ipsquare.hibernate;

import java.sql.Driver;

/**
 * The database configuration. 
 * 
 * @author Matthias Langer
 */
public interface HibernateConfiguration
{
    String getDbUser();
    String getDbPass();
    Class<?>[] getDomainClasses();
    String getDbConnectionUrl();
    Class<? extends Driver> getDbDriverClass();
    HibernateHbm2dllAuto getHbm2dllAuto();
}
