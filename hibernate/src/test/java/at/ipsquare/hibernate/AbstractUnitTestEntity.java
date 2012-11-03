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

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import at.ipsquare.interfaces.HasId;

/**
 * Abstract base class for all unit test entities.
 * 
 * @author Matthias Langer
 */
@MappedSuperclass
public class AbstractUnitTestEntity implements HasId<Long>
{
    @Id
    @GeneratedValue
    private Long id;
    
    public Long getId()
    {
        return id;
    }
}