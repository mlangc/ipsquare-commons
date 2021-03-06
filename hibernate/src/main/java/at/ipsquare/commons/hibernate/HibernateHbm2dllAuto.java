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

/**
 * This enum represents the 'hibernate.hbm2ddl.auto' Hibernate configuration property.
 * 
 * @since 2.0.0
 */
public enum HibernateHbm2dllAuto
{
    VALIDATE("validate"), UPDATE("update"), CREATE("create"), CREATE_DROP("create-drop");
    
    private final String value;
    
    private HibernateHbm2dllAuto(String value)
    {
        if(value == null)
            throw new NullPointerException();
        
        this.value = value;
    }
    
    @Override
    public String toString()
    {
        return value;
    }
}
