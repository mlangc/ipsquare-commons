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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Entity for unit tests.
 * 
 * @author Matthias Langer
 */
@Entity
public class UnitTestEntityChild extends AbstractUnitTestEntity
{
    @Column(unique = true, nullable = false)
    private String name;
    
    @ManyToOne(cascade = CascadeType.ALL)
    private UnitTestEntityParent parent;
    
    public UnitTestEntityChild()
    {
        
    }
    
    public UnitTestEntityChild(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
    public UnitTestEntityParent getParent()
    {
        return parent;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setParent(UnitTestEntityParent parent)
    {
        this.parent = parent;
    }
}
