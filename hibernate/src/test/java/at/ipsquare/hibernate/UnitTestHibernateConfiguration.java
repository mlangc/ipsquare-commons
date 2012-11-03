/**
 * Copyright (C) 2012 Matthias Langer <mlangc@gmx.at>
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
