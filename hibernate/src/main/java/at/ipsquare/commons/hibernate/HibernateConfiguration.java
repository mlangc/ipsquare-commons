/**
 * Copyright (C) 2012 IP SQUARE
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

import java.sql.Driver;
import java.util.Map;

import org.hibernate.dialect.Dialect;

/**
 * The database configuration.
 * 
 * </p>
 * See http://docs.jboss.org/hibernate/orm/4.1/devguide/en-US/html/apa.html
 * 
 * @since 1.0.0
 * @author Matthias Langer
 */
public interface HibernateConfiguration
{
    /**
     * The database user.
     * 
     * @since 1.0.0
     */
    String getDbUser();
    
    /**
     * The password for the user.
     * 
     * @since 1.0.0
     */
    String getDbPass();
    
    /**
     * The domain classes.
     * 
     * @since 1.0.0
     */
    Class<?>[] getDomainClasses();
    
    /**
     * The connection URL.
     * 
     * @since 1.0.0
     */
    String getDbConnectionUrl();

    /**
     * The database driver.
     * 
     * <h5>Note:</h5>
     *  The class you return from this method will explicitly initialized by {@link DefaultHibernateRepository}
     *  to avoid <a href="http://stackoverflow.com/questions/160611/cause-of-no-suitable-driver-found-for">problems</a> later on.
     *  
     *  @since 1.0.0
     */
    Class<? extends Driver> getDbDriverClass();
    
    /**
     * The hibernate dialect to use.
     * 
     * @since 2.0.0
     */
    Class<? extends Dialect> getDbDialectClass();
    
    /**
     * The value of {@literal hibernate.hbm2ddl.auto}.
     * 
     * <p/>
     * See <a href='http://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch03.html#configuration-optional'>Hibernate configuration</a>.
     * 
     * @since 1.0.0
     */
    HibernateHbm2dllAuto getHbm2dllAuto();
    
    /**
     * A map of arbitrary hibernate configuration properties.
     * 
     * @since 1.0.0
     */
    Map<String, String> getProperties();
}
